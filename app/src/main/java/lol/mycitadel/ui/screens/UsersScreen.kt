package lol.mycitadel.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import lol.mycitadel.app.data.network.UserDto
import lol.mycitadel.app.data.network.UserSummary
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.theme.*
import lol.mycitadel.app.ui.users.DirectoryFilter
import lol.mycitadel.app.ui.users.UsersState
import lol.mycitadel.app.ui.users.UsersViewModel
import lol.mycitadel.app.ui.components.CitizenAction
import lol.mycitadel.app.ui.components.CitizenActionButton
import androidx.compose.foundation.layout.RowScope
private const val DEFAULT_AVATAR    = "https://mycitadel.lol/img/users/default/avatar.png"
private const val DEFAULT_BANNER    = "https://mycitadel.lol/img/users/default/banner.png"
private const val DEFAULT_WALLPAPER = "https://mycitadel.lol/img/users/default/wallpaper.png"

@Composable
fun UsersScreen(
    currentUser: UserDto?,
    onOpenProfile: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UsersViewModel = viewModel(factory = UsersViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(currentUser) { viewModel.setSelf(currentUser) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.loading && state.users.isEmpty() && state.self == null -> LoadingState()
            state.fatalError != null && state.users.isEmpty() -> ErrorState(state.fatalError!!, viewModel::refresh)
            else -> DirectoryContent(state, viewModel, onOpenProfile)
        }

        state.toast?.let { msg ->
            ToastOverlay(msg, state.toastIsError, viewModel::clearToast)
        }
    }
}

/* ══════════════════════════════════════════════════════════════════ */

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
            Spacer(Modifier.height(16.dp))
            Text("LOADING CITIZENS…", color = TextDim, fontSize = 11.sp, letterSpacing = 3.sp)
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 48.sp, color = Blood)
            Spacer(Modifier.height(16.dp))
            Text("Could not load Citizens", color = GoldBright, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(message, color = TextDim, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            CitadelButton("Retry", onRetry, style = CitadelButtonStyle.Cyan)
        }
    }
}

/* ══════════════════════════════════════════════════════════════════ */

@Composable
private fun DirectoryContent(
    state: UsersState,
    vm: UsersViewModel,
    onOpenProfile: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {

        // ── Search bar ─────────────────────────────────────────
        OutlinedTextField(
            value = state.query,
            onValueChange = vm::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            placeholder = { Text("Search Citizens…", color = TextFaint, fontSize = 14.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Cyan,
                unfocusedBorderColor = Cyan.copy(alpha = 0.25f),
                cursorColor = CyanBright,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(10.dp),
        )

        // ── Filter chips ───────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DirectoryFilter.entries.forEach { f ->
                val active = state.filter == f
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) Cyan else Color.Transparent)
                        .border(1.dp, Cyan.copy(alpha = 0.6f), RoundedCornerShape(999.dp))
                        .clickable { vm.onFilterChange(f) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = f.label.uppercase(),
                        color = if (active) Void else Cyan,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (state.total > 0) {
                Text(
                    text = "${state.total}",
                    color = TextFaint,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        // ── Grid ───────────────────────────────────────────────
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Self card pinned at top (spans full width)
            state.self?.let { me ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SelfCard(
                        user = me,
                        onView = { onOpenProfile(me.id) },
                    )
                }
            }

            items(state.users, key = { it.id }) { user ->
                CitizenCard(
                    user = user,
                    onOpenProfile = { onOpenProfile(user.id) },
                    onRequest = { vm.requestConnection(user) },
                    onAccept = { vm.acceptConnection(user) },
                    onCancel = { vm.cancelRequest(user) },
                    onDeny = { vm.denyRequest(user) },
                    onSever = { vm.severConnection(user) },
                    onHide = { vm.hideUser(user) },
                )
            }

            if (state.hasMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        Modifier.fillMaxWidth().padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state.loadingMore) {
                            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                        } else {
                            CitadelButton(
                                text = "Load More",
                                onClick = vm::loadMore,
                                style = CitadelButtonStyle.Cyan,
                            )
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(24.dp)) }
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * SELF CARD
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun SelfCard(user: UserDto, onView: () -> Unit) {
    CitizenCardBase(
        displayName = user.displayName ?: user.username,
        username    = user.username,
        tagline     = user.tagline,
        avatarUrl   = user.avatarUrl ?: DEFAULT_AVATAR,
        bannerUrl   = user.bannerUrl ?: DEFAULT_BANNER,
        wallpaperUrl = DEFAULT_WALLPAPER,
        reputation  = user.reputation,
        badgeCount  = 0,
        isSelf      = true,
    ) {
        CitizenActionButton("View My Profile", CitizenAction.Cyan, onView)
        CitizenActionButton("Edit Profile", CitizenAction.Gold, onView)
    }
}

/* ══════════════════════════════════════════════════════════════════
 * CITIZEN CARD
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun CitizenCard(
    user: UserSummary,
    onOpenProfile: () -> Unit,
    onRequest: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onDeny: () -> Unit,
    onSever: () -> Unit,
    onHide: () -> Unit,
) {
    CitizenCardBase(
        displayName = user.displayName ?: user.username,
        username    = user.username,
        tagline     = user.tagline,
        avatarUrl   = user.avatarUrl ?: DEFAULT_AVATAR,
        bannerUrl   = user.bannerUrl ?: DEFAULT_BANNER,
        wallpaperUrl = user.wallpaperUrl ?: DEFAULT_WALLPAPER,
        reputation  = user.reputation,
        badgeCount  = user.badgeCount,
        isSelf      = false,
    ) {
        when (user.connectionState) {
            "connected" -> {
                CitizenActionButton("View Profile", CitizenAction.Cyan, onOpenProfile)
                CitizenActionButton("Sever", CitizenAction.Danger, onSever)
            }
            "pending_out" -> {
                CitizenActionButton("Request Sent", CitizenAction.Muted, {})
                CitizenActionButton("Cancel", CitizenAction.Ghost, onCancel)
            }
            "pending_in" -> {
                CitizenActionButton("Accept", CitizenAction.Cyan, onAccept)
                CitizenActionButton("Deny", CitizenAction.Danger, onDeny)
            }
            else -> {
                CitizenActionButton("Request Connection", CitizenAction.Cyan, onRequest)
                CitizenActionButton("Hide Me", CitizenAction.Ghost, onHide)
            }
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * CARD SHELL — shared visual
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun CitizenCardBase(
    displayName: String,
    username: String,
    tagline: String?,
    avatarUrl: String,
    bannerUrl: String,
    wallpaperUrl: String,
    reputation: Int,
    badgeCount: Int,
    isSelf: Boolean,
    actions: @Composable RowScope.() -> Unit,
) {
    val borderColor = if (isSelf) Gold.copy(alpha = 0.55f) else Cyan.copy(alpha = 0.18f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Void)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
    ) {
        // Wallpaper background layer
        AsyncImage(
            model = wallpaperUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.12f,
            modifier = Modifier.matchParentSize(),
        )

        Column {
            // ── Banner strip ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            ) {
                AsyncImage(
                    model = bannerUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.55f,
                    modifier = Modifier.matchParentSize(),
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Void.copy(alpha = 0.92f),
                                )
                            )
                        ),
                )
                if (isSelf) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Gold)
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                    ) {
                        Text(
                            "YOU",
                            color = Void,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                        )
                    }
                }
            }

            // ── Avatar + meta ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .offset(y = (-32).dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, Gold, CircleShape),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "@$username",
                        color = Cyan,
                        fontSize = 11.sp,
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                    )
                }
            }

            // ── Tagline + stats ───────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                if (!tagline.isNullOrBlank()) {
                    Text(
                        text = tagline,
                        color = TextDim,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }

                if (reputation > 0 || badgeCount > 0) {
                    val bits = buildList {
                        if (reputation > 0) add("★ ${"%,d".format(reputation)}")
                        if (badgeCount > 0) add("$badgeCount badges")
                    }
                    Text(
                        text = bits.joinToString(" · "),
                        color = TextFaint,
                        fontSize = 10.sp,
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                    )
                }
            }

            // ── Divider + actions ─────────────────────────────────
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Cyan.copy(alpha = 0.1f)),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                actions()
            }
        }
    }
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