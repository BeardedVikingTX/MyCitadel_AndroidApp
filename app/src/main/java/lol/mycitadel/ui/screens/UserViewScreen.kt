package lol.mycitadel.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import lol.mycitadel.app.data.network.*
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.theme.*
import lol.mycitadel.app.ui.users.UserViewState
import lol.mycitadel.app.ui.users.UserViewModel
import lol.mycitadel.app.ui.components.CitizenAction
import lol.mycitadel.app.ui.components.CitizenActionButton
private const val DEFAULT_AVATAR = "https://mycitadel.lol/img/users/default/avatar.png"
private const val DEFAULT_BANNER = "https://mycitadel.lol/img/users/default/banner.png"

@Composable
fun UserViewScreen(
    userId: Int,
    onBack: () -> Unit,
    onEditOwnProfile: () -> Unit,
    onOpenDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel(factory = UserViewModel.factory(userId)),
) {
    val state by viewModel.state.collectAsState()
    val ctx = LocalContext.current

    // Auto-navigate back when the profile becomes unreachable after an action
    LaunchedEffect(state.toast) {
        if (state.toast != null && !state.toastIsError &&
            (state.toast == "Connection severed." ||
                    state.toast == "Hidden." ||
                    state.toast == "Request denied.")) {
            delay(900)
            onBack()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.loading -> LoadingView()
            state.notFound -> NotFoundView(onBack)
            state.error != null -> ErrorView(state.error!!, viewModel::load)
            state.profile != null -> {
                val p = state.profile!!
                ProfileBody(
                    profile = p,
                    view = state.view,
                    busy = state.actionBusy,
                    onBack = onBack,
                    onEditOwnProfile = onEditOwnProfile,
                    onOpenDashboard = onOpenDashboard,
                    onRequest = viewModel::requestConnection,
                    onAccept = viewModel::acceptConnection,
                    onCancel = viewModel::cancelRequest,
                    onDeny = viewModel::denyRequest,
                    onSever = viewModel::severConnection,
                    onHide = viewModel::hideUser,
                    onOpenUrl = { url ->
                        try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                        catch (_: Exception) { /* no handler */ }
                    },
                )
            }
        }

        state.toast?.let { msg ->
            ToastOverlay(msg, state.toastIsError, viewModel::clearToast)
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * STATUS VIEWS
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
            Spacer(Modifier.height(16.dp))
            Text("LOADING PROFILE…", color = TextDim, fontSize = 11.sp, letterSpacing = 3.sp)
        }
    }
}

@Composable
private fun NotFoundView(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ᚱ", fontSize = 60.sp, color = Gold.copy(alpha = 0.6f))
            Spacer(Modifier.height(16.dp))
            Text("Citizen Not Found", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(
                "This Citizen does not exist, or you do not have permission to view them.",
                color = TextDim, fontSize = 13.sp, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            CitadelButton("Back to Citizens", onBack, style = CitadelButtonStyle.Cyan)
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 48.sp, color = Blood)
            Spacer(Modifier.height(16.dp))
            Text("Could Not Load Profile", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(message, color = TextDim, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            CitadelButton("Retry", onRetry, style = CitadelButtonStyle.Cyan)
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * PROFILE BODY
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ProfileBody(
    profile: ProfileFull,
    view: String,
    busy: Boolean,
    onBack: () -> Unit,
    onEditOwnProfile: () -> Unit,
    onOpenDashboard: () -> Unit,
    onRequest: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onDeny: () -> Unit,
    onSever: () -> Unit,
    onHide: () -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "← Back",
                    color = Cyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onBack).padding(6.dp),
                )
            }
        }

        item { HeroCard(profile) }
        item { StatsStrip(profile) }

        item {
            ActionsRow(
                profile = profile,
                busy = busy,
                onEditOwnProfile = onEditOwnProfile,
                onOpenDashboard = onOpenDashboard,
                onRequest = onRequest,
                onAccept = onAccept,
                onCancel = onCancel,
                onDeny = onDeny,
                onSever = onSever,
                onHide = onHide,
            )
        }

        if (view == "limited") {
            item {
                CitadelPanel(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    borderColor = Gold.copy(alpha = 0.4f),
                ) {
                    Text(
                        text = "${profile.displayName ?: profile.username} has limited their profile to connections.",
                        color = TextDim,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                    )
                }
            }
        } else {
            // About
            val aboutHtml = listOfNotNull(
                profile.bio?.takeIf { it.isNotBlank() },
                profile.personalMotto?.takeIf { it.isNotBlank() }?.let { "\"$it\"" },
            )
            if (aboutHtml.isNotEmpty()) item { TextSection("About", aboutHtml) }

            // Work
            profile.work?.let { w ->
                val rows = buildList {
                    w.jobTitle?.takeIf { it.isNotBlank() }?.let { add("Title" to it) }
                    w.company?.takeIf { it.isNotBlank() }?.let { add("Company" to it) }
                    w.yearsAtCompany?.let { add("Years" to it.toString()) }
                    w.industry?.takeIf { it.isNotBlank() }?.let { add("Industry" to it) }
                    w.education?.takeIf { it.isNotBlank() }?.let { add("Education" to it) }
                }
                if (rows.isNotEmpty() || !w.description.isNullOrBlank()) {
                    item {
                        DetailSection("Work", rows, w.description)
                    }
                }
            }

            // Personal
            profile.personal?.let { pe ->
                val rows = buildList {
                    pe.relationshipStatus?.takeIf { it.isNotBlank() }?.let { add("Status" to it.replace("_", " ")) }
                    if (pe.hasKids == true) add("Kids" to (pe.kidsCount?.toString() ?: "Yes"))
                    pe.languagesSpoken?.takeIf { it.isNotBlank() }?.let { add("Languages" to it) }
                    pe.personalityType?.takeIf { it.isNotBlank() }?.let { add("Personality" to it) }
                    pe.zodiacSign?.takeIf { it.isNotBlank() }?.let { add("Zodiac" to it) }
                    pe.availability?.takeIf { it.isNotBlank() }?.let { add("Availability" to it.replace("_", " ")) }
                    pe.contactPreference?.takeIf { it.isNotBlank() }?.let { add("Contact via" to it.replace("_", " ")) }
                }
                if (rows.isNotEmpty()) item { DetailSection("Personal", rows, null) }
            }

            // Interests
            profile.interests?.let { itr ->
                if (!itr.hobbies.isNullOrBlank() || itr.lookingFor.isNotEmpty()) {
                    item { InterestsSection(itr) }
                }
            }

            // Favorites
            profile.favorites?.let { fav ->
                item { FavoritesSection(fav) }
            }

            // Links
            profile.links?.let { lk ->
                val entries = lk.entries()
                if (entries.isNotEmpty()) item { LinksSection(entries, onOpenUrl) }
            }
        }

        if (profile.badges.isNotEmpty()) {
            item { BadgesSection(profile.badges) }
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * HERO
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun HeroCard(profile: ProfileFull) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Void)
                .border(1.dp, Cyan.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
        ) {
            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
            ) {
                AsyncImage(
                    model = profile.bannerUrl ?: DEFAULT_BANNER,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Void.copy(alpha = 0.9f),
                                )
                            )
                        ),
                )
            }

            // Avatar + identity (overlapping banner bottom)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .offset(y = (-55).dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                AsyncImage(
                    model = profile.avatarUrl ?: DEFAULT_AVATAR,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, Gold, CircleShape),
                )
            }

            // Name + handle + tagline + pills
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .offset(y = (-45).dp),
            ) {
                Text(
                    text = profile.displayName ?: profile.username,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${profile.username}",
                        color = Cyan,
                        fontSize = 13.sp,
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                    )
                    profile.pronouns?.takeIf { it.isNotBlank() }?.let {
                        Text(" · $it", color = TextFaint, fontSize = 12.sp)
                    }
                }
                profile.tagline?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = TextDim, fontSize = 13.sp, fontStyle = FontStyle.Italic, lineHeight = 18.sp)
                }

                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (profile.isSelf) Pill("YOU", Gold)
                    if (profile.connectionState == "connected") Pill("CONNECTED", Success)
                    if (profile.isPremium) Pill("★ PREMIUM", Gold)
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Member since ${fmtDate(profile.memberSince)}",
                    color = TextFaint,
                    fontSize = 10.sp,
                    fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                )
            }
        }
    }
}

@Composable
private fun Pill(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(label, color = color, fontSize = 9.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.SemiBold)
    }
}

/* ══════════════════════════════════════════════════════════════════
 * STATS STRIP
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun StatsStrip(profile: ProfileFull) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        StatTile("Reputation", "%,d".format(profile.reputation), Modifier.weight(1f))
        StatTile("Badges", profile.badgeCount.toString(), Modifier.weight(1f))
        StatTile("Posts", profile.postCount.toString(), Modifier.weight(1f))
        StatTile("Connections", profile.connectionCount.toString(), Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Void.copy(alpha = 0.6f))
            .border(1.dp, Cyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = Cyan, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(2.dp))
        Text(label.uppercase(), color = TextFaint, fontSize = 8.sp, letterSpacing = 1.sp)
    }
}

/* ══════════════════════════════════════════════════════════════════
 * ACTIONS
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ActionsRow(
    profile: ProfileFull,
    busy: Boolean,
    onEditOwnProfile: () -> Unit,
    onOpenDashboard: () -> Unit,
    onRequest: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onDeny: () -> Unit,
    onSever: () -> Unit,
    onHide: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (profile.isSelf) {
            ActionButton("Edit Profile", CitizenAction.Gold, !busy, onEditOwnProfile, Modifier.weight(1f))
            ActionButton("My Dashboard", CitizenAction.Cyan, !busy, onOpenDashboard, Modifier.weight(1f))
            return
        }
        when (profile.connectionState) {
            "connected" -> {
                ActionButton("Sever", CitizenAction.Danger, !busy, onSever, Modifier.weight(1f))
                ActionButton("Message", CitizenAction.Gold, !busy, { /* later */ }, Modifier.weight(1f))
            }
            "pending_out" -> {
                ActionButton("Request Sent", CitizenAction.Muted, false, {}, Modifier.weight(1f))
                ActionButton("Cancel", CitizenAction.Ghost, !busy, onCancel, Modifier.weight(1f))
            }
            "pending_in" -> {
                ActionButton("Accept", CitizenAction.Cyan, !busy, onAccept, Modifier.weight(1f))
                ActionButton("Deny", CitizenAction.Danger, !busy, onDeny, Modifier.weight(1f))
            }
            else -> {
                ActionButton("Request Connection", CitizenAction.Cyan, !busy, onRequest, Modifier.weight(1f))
                ActionButton("Hide Me", CitizenAction.Ghost, !busy, onHide, Modifier.weight(1f))
            }
        }
    }
}


@Composable
private fun ActionButton(
    label: String,
    style: CitizenAction,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (border, text) = when (style) {
        CitizenAction.Cyan   -> Cyan to Cyan
        CitizenAction.Gold   -> Gold to Gold
        CitizenAction.Danger -> Blood to Blood
        CitizenAction.Ghost  -> TextDim.copy(alpha = 0.55f) to TextDim
        CitizenAction.Muted  -> TextFaint.copy(alpha = 0.5f) to TextFaint
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = text,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * SECTION HELPERS
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun TextSection(title: String, paragraphs: List<String>) {
    CitadelPanel(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
        SectionTitle(title)
        paragraphs.forEach { p ->
            Text(p, color = TextDim, fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DetailSection(title: String, rows: List<Pair<String, String>>, body: String?) {
    CitadelPanel(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
        SectionTitle(title)
        rows.forEach { (label, value) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(
                    label.uppercase(),
                    color = TextFaint,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.width(110.dp),
                )
                Text(value, color = TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
            }
        }
        if (!body.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(body, color = TextDim, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
private fun InterestsSection(interests: ProfileInterests) {
    CitadelPanel(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
        SectionTitle("Interests")
        interests.hobbies?.takeIf { it.isNotBlank() }?.let {
            Text(it, color = TextDim, fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(10.dp))
        }
        if (interests.lookingFor.isNotEmpty()) {
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                interests.lookingFor.forEach { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Cyan.copy(alpha = 0.08f))
                            .border(1.dp, Cyan.copy(alpha = 0.4f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                    ) {
                        Text(
                            item.replace("_", " "),
                            color = Cyan,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritesSection(fav: ProfileFavorites) {
    val blocks = buildList {
        if (fav.movies.isNotEmpty()) add("Movies" to fav.movies)
        if (fav.books.isNotEmpty())  add("Books" to fav.books)
        if (fav.songs.isNotEmpty())  add("Songs" to fav.songs)
        if (fav.shows.isNotEmpty())  add("Shows" to fav.shows)
        if (fav.games.isNotEmpty())  add("Games" to fav.games)
    }
    if (blocks.isEmpty() && fav.quotes.isNullOrBlank() && fav.food.isNullOrBlank()) return

    CitadelPanel(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
        SectionTitle("Favorites")
        blocks.forEach { (label, items) ->
            Text(
                label.uppercase(),
                color = Cyan,
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(6.dp))
            items.forEach { item ->
                Row(modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)) {
                    Text("•", color = Cyan.copy(alpha = 0.5f), fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(item, color = TextDim, fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        fav.quotes?.takeIf { it.isNotBlank() }?.let {
            Text("Quote", color = Cyan, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("\"$it\"", color = TextPrimary, fontSize = 14.sp, fontStyle = FontStyle.Italic, lineHeight = 21.sp)
            Spacer(Modifier.height(10.dp))
        }
        fav.food?.takeIf { it.isNotBlank() }?.let {
            Text("Food", color = Cyan, fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(it, color = TextDim, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LinksSection(entries: List<Pair<String, String>>, onOpenUrl: (String) -> Unit) {
    CitadelPanel(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
        SectionTitle("Links")
        entries.forEach { (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable {
                        if (value.startsWith("http", ignoreCase = true)) onOpenUrl(value)
                    },
            ) {
                Text(
                    label.uppercase(),
                    color = TextFaint,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.width(120.dp),
                )
                Text(
                    value,
                    color = Cyan,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BadgesSection(badges: List<ProfileBadge>) {
    CitadelPanel(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
        SectionTitle("Badges (${badges.size})")
        badges.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { b -> BadgeTile(b, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BadgeTile(badge: ProfileBadge, modifier: Modifier = Modifier) {
    val accent = parseColor(badge.color) ?: Cyan
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Void.copy(alpha = 0.6f))
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(10.dp),
    ) {
        Text(badge.tier.uppercase(), color = accent, fontSize = 9.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(badge.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        if (badge.description.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(badge.description, color = TextFaint, fontSize = 10.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = GoldBright,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

/* ══════════════════════════════════════════════════════════════════
 * TOAST
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun ToastOverlay(message: String, isError: Boolean, onDismiss: () -> Unit) {
    LaunchedEffect(message) {
        delay(2400)
        onDismiss()
    }
    val accent = if (isError) Blood else Success
    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Void)
                .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Text(
                text = message,
                color = if (isError) Color(0xFFFFB0B0) else Color(0xFFB0FFA8),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * HELPERS
 * ════════════════════════════════════════════════════════════════ */

private fun fmtDate(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val instant = java.time.Instant.parse(iso)
        java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")
            .withZone(java.time.ZoneId.systemDefault())
            .format(instant)
    } catch (_: Exception) { iso.take(10) }
}

/** Parse a hex color like "#00e5ff". Returns null if invalid. */
private fun parseColor(hex: String?): Color? {
    if (hex == null) return null
    return try {
        val clean = hex.trimStart('#')
        val value = when (clean.length) {
            6 -> clean.toLong(16) or 0xFF000000L
            8 -> clean.toLong(16)
            3 -> {
                val r = clean[0].toString().repeat(2)
                val g = clean[1].toString().repeat(2)
                val b = clean[2].toString().repeat(2)
                "$r$g$b".toLong(16) or 0xFF000000L
            }
            else -> return null
        }
        Color(value)
    } catch (_: Exception) { null }
}