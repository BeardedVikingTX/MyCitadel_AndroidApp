package lol.mycitadel.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import lol.mycitadel.app.ui.components.CitadelButton
import lol.mycitadel.app.ui.components.CitadelButtonStyle
import lol.mycitadel.app.ui.components.CitadelPanel
import lol.mycitadel.app.ui.components.RuneDivider
import lol.mycitadel.app.ui.profile.ProfileEditState
import lol.mycitadel.app.ui.profile.ProfileSection
import lol.mycitadel.app.ui.profile.ProfileViewModel
import lol.mycitadel.app.ui.profile.SaveStatus
import lol.mycitadel.app.ui.profile.boolField
import lol.mycitadel.app.ui.profile.stringField
import lol.mycitadel.app.ui.profile.stringListField
import lol.mycitadel.app.ui.theme.Blood
import lol.mycitadel.app.ui.theme.Cyan
import lol.mycitadel.app.ui.theme.CyanBright
import lol.mycitadel.app.ui.theme.Gold
import lol.mycitadel.app.ui.theme.GoldBright
import lol.mycitadel.app.ui.theme.Success
import lol.mycitadel.app.ui.theme.TextDim
import lol.mycitadel.app.ui.theme.TextFaint
import lol.mycitadel.app.ui.theme.Void

private const val DEFAULT_AVATAR = "https://mycitadel.lol/img/users/default/avatar.png"

@Composable
fun ProfileEditScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.loading -> LoadingView()
            state.loadError != null -> ErrorView(state.loadError!!, viewModel::load)
            else -> MainContent(state, viewModel, onBack)
        }

        // Save status banner — appears at the bottom
        SaveStatusBanner(state.saveStatus, state.errorMessage)
    }
}

/* ══════════════════════════════════════════════════════════════════
 * LOADING / ERROR
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "LOADING YOUR PROFILE…",
                style = MaterialTheme.typography.labelSmall,
                color = TextDim,
                letterSpacing = 3.sp,
            )
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠", fontSize = 48.sp, color = Blood)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Could not load profile",
                style = MaterialTheme.typography.titleLarge,
                color = GoldBright,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDim,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            CitadelButton(
                text = "Retry",
                onClick = onRetry,
                style = CitadelButtonStyle.Cyan,
            )
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * MAIN CONTENT
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun MainContent(
    state: ProfileEditState,
    vm: ProfileViewModel,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // Back + title row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "← Back",
                color = Cyan,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(6.dp),
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "EDIT PROFILE",
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
                letterSpacing = 3.sp,
            )
        }

        // Section tabs
        val sections = ProfileSection.entries
        ScrollableTabRow(
            selectedTabIndex = sections.indexOf(state.activeSection),
            edgePadding = 12.dp,
            containerColor = Void,
            contentColor = Cyan,
            divider = {},
        ) {
            sections.forEach { section ->
                Tab(
                    selected = state.activeSection == section,
                    onClick = { vm.selectSection(section) },
                    text = {
                        Text(
                            text = section.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (state.activeSection == section) Cyan else TextDim,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp,
                        )
                    },
                )
            }
        }

        // Section body
        when (state.activeSection) {
            ProfileSection.Identity     -> IdentitySection(state, vm)
            ProfileSection.Images       -> ImagesSection(state, vm)
            ProfileSection.Location     -> LocationSection(state, vm)
            ProfileSection.Personal     -> PersonalSection(state, vm)
            ProfileSection.Work         -> WorkSection(state, vm)
            ProfileSection.Life         -> LifeSection(state, vm)
            ProfileSection.Interests    -> InterestsSection(state, vm)
            ProfileSection.Favorites    -> FavoritesSection(state, vm)
            ProfileSection.Links        -> LinksSection(state, vm)
            ProfileSection.Theme        -> ThemeSection(state, vm)
            ProfileSection.Privacy      -> PrivacySection(state, vm)
            ProfileSection.Account      -> AccountSection(state, vm)
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * REUSABLE FIELD
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun Field(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true,
    minLines: Int = 1,
    hint: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Cyan,
                letterSpacing = 1.sp,
                fontSize = 10.sp,
            )
        },
        placeholder = { Text(text = placeholder, color = TextFaint, fontSize = 13.sp) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan,
            unfocusedBorderColor = Cyan.copy(alpha = 0.25f),
            focusedLabelColor = CyanBright,
            unfocusedLabelColor = Cyan.copy(alpha = 0.7f),
            cursorColor = CyanBright,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(8.dp),
        supportingText = hint?.let {
            { Text(text = it, color = TextFaint, fontSize = 10.sp) }
        },
    )
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun SectionScaffold(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp
        ),
    ) {
        item {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = GoldBright,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDim,
                )
                Spacer(Modifier.height(20.dp))
                content()
            }
        }
    }
}

/* ══════════════════════════════════════════════════════════════════
 * SECTIONS
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun IdentitySection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Identity", "How the Citadel sees you.") {
        Field("Display Name", p.stringField("display_name"),
            { vm.onFieldChange("display_name", it) }, "Bearded Viking")
        Field("Tagline", p.stringField("tagline"),
            { vm.onFieldChange("tagline", it) },
            "Self-taught builder. Father of four.")
        Field("Pronouns", p.stringField("pronouns"),
            { vm.onFieldChange("pronouns", it) }, "he/him")
        Field("Personal Motto", p.stringField("personal_motto"),
            { vm.onFieldChange("personal_motto", it) }, "Fortune favors the bold")
        Field("Bio", p.stringField("bio"),
            { vm.onFieldChange("bio", it) },
            "Tell the Citadel who you are…",
            singleLine = false, minLines = 5)
    }
}

@Composable
private fun ImagesSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile

    val pickAvatar = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { vm.uploadImage("avatar", it) } }

    val pickBanner = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { vm.uploadImage("banner", it) } }

    val pickWallpaper = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { vm.uploadImage("wallpaper", it) } }

    SectionScaffold("Images", "Max 5 MB. JPG, PNG, or WebP.") {

        ImageSlot(
            label = "Avatar",
            url = p.stringField("avatar_url").takeIf { it.isNotBlank() } ?: DEFAULT_AVATAR,
            isCircle = true,
            uploading = state.uploadingKind == "avatar",
            onPick = { pickAvatar.launch("image/*") },
        )
        Spacer(Modifier.height(20.dp))

        ImageSlot(
            label = "Banner",
            url = p.stringField("banner_url"),
            isCircle = false,
            uploading = state.uploadingKind == "banner",
            onPick = { pickBanner.launch("image/*") },
        )
        Spacer(Modifier.height(20.dp))

        ImageSlot(
            label = "Wallpaper",
            url = p.stringField("wallpaper_url"),
            isCircle = false,
            uploading = state.uploadingKind == "wallpaper",
            onPick = { pickWallpaper.launch("image/*") },
        )
    }
}

@Composable
private fun ImageSlot(
    label: String,
    url: String?,
    isCircle: Boolean,
    uploading: Boolean,
    onPick: () -> Unit,
) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Cyan,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(8.dp))

        val shape = if (isCircle) CircleShape else RoundedCornerShape(12.dp)
        val sizeMod = if (isCircle) Modifier.size(120.dp)
        else Modifier.fillMaxWidth().height(140.dp)

        Box(
            modifier = sizeMod
                .clip(shape)
                .background(Void)
                .border(1.dp, Cyan.copy(alpha = 0.3f), shape),
            contentAlignment = Alignment.Center,
        ) {
            if (url.isNullOrBlank()) {
                Text("No image set", color = TextFaint, fontSize = 12.sp)
            } else {
                AsyncImage(
                    model = url,
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (uploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Cyan, strokeWidth = 2.dp)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        CitadelButton(
            text = if (url.isNullOrBlank()) "Choose $label" else "Change $label",
            onClick = onPick,
            style = CitadelButtonStyle.Cyan,
            enabled = !uploading,
        )
    }
}

@Composable
private fun LocationSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Location", "Where you're from.") {
        Field("Country Code", p.stringField("country_code"),
            { vm.onFieldChange("country_code", it.uppercase()) }, "US")
        Field("State / Region", p.stringField("state_code"),
            { vm.onFieldChange("state_code", it.uppercase()) }, "TX")
        Field("Timezone", p.stringField("timezone"),
            { vm.onFieldChange("timezone", it) }, "America/Chicago")
    }
}

@Composable
private fun PersonalSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Personal", "Encrypted with a key tied to your account.") {
        Field("First Name", p.stringField("first_name"), { vm.onFieldChange("first_name", it) })
        Field("Middle Name", p.stringField("middle_name"), { vm.onFieldChange("middle_name", it) })
        Field("Last Name", p.stringField("last_name"), { vm.onFieldChange("last_name", it) })
        Field("Phone", p.stringField("phone"), { vm.onFieldChange("phone", it) },
            keyboardType = KeyboardType.Phone)
        Field("Backup Email", p.stringField("backup_email"), { vm.onFieldChange("backup_email", it) },
            keyboardType = KeyboardType.Email)
        Field("Recovery Phone", p.stringField("recovery_phone"),
            { vm.onFieldChange("recovery_phone", it) }, keyboardType = KeyboardType.Phone)
        Field("Birthday", p.stringField("birthday"), { vm.onFieldChange("birthday", it) },
            placeholder = "1990-01-15")
        Field("Birth Year", p.stringField("birth_year"), { vm.onFieldChange("birth_year", it) },
            keyboardType = KeyboardType.Number)
        Field("Address Line 1", p.stringField("address_line1"), { vm.onFieldChange("address_line1", it) })
        Field("Address Line 2", p.stringField("address_line2"), { vm.onFieldChange("address_line2", it) })
        Field("City", p.stringField("city"), { vm.onFieldChange("city", it) })
        Field("ZIP / Postal", p.stringField("zip"), { vm.onFieldChange("zip", it) })
    }
}

@Composable
private fun WorkSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Work", "What you do day to day.") {
        Field("Job Title", p.stringField("job_title"), { vm.onFieldChange("job_title", it) })
        Field("Company", p.stringField("company_name"), { vm.onFieldChange("company_name", it) })
        Field("Years at Company", p.stringField("years_at_company"),
            { vm.onFieldChange("years_at_company", it) }, keyboardType = KeyboardType.Number)
        Field("Industry", p.stringField("industry"), { vm.onFieldChange("industry", it) })
        Field("Education", p.stringField("education"), { vm.onFieldChange("education", it) })
        Field("Work Description", p.stringField("work_description"),
            { vm.onFieldChange("work_description", it) },
            singleLine = false, minLines = 4)
    }
}

@Composable
private fun LifeSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Personal Life", "The things that make you, you.") {

        SelectField(
            label = "Relationship Status",
            current = p.stringField("relationship_status"),
            options = state.options.relationshipStatuses,
            onSelect = { vm.onFieldChange("relationship_status", it) },
        )
        Field("Personality Type", p.stringField("personality_type"),
            { vm.onFieldChange("personality_type", it) }, "INTJ")
        Field("Zodiac Sign", p.stringField("zodiac_sign"),
            { vm.onFieldChange("zodiac_sign", it) }, "Scorpio")
        Field("Languages Spoken", p.stringField("languages_spoken"),
            { vm.onFieldChange("languages_spoken", it) }, "English, Spanish")

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = p.boolField("has_kids"),
                onCheckedChange = { vm.onFieldChange("has_kids", it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Cyan,
                    uncheckedColor = Cyan.copy(alpha = 0.4f),
                    checkmarkColor = Void,
                ),
            )
            Text("I have kids", color = TextDim)
        }
        Spacer(Modifier.height(4.dp))

        if (p.boolField("has_kids")) {
            Field("Number of Kids", p.stringField("kids_count"),
                { vm.onFieldChange("kids_count", it) }, keyboardType = KeyboardType.Number)
        }
    }
}

@Composable
private fun InterestsSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Interests", "What you're looking for and how to reach you.") {

        Text(
            text = "LOOKING FOR",
            style = MaterialTheme.typography.labelSmall,
            color = Cyan,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(10.dp))

        val selected = p.stringListField("looking_for")
        val chipRowMod = Modifier.fillMaxWidth()
        androidx.compose.foundation.layout.FlowRow(
            modifier = chipRowMod,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.options.lookingForOptions.forEach { option ->
                val isOn = option in selected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isOn) Cyan else Color.Transparent)
                        .border(1.dp, Cyan, RoundedCornerShape(999.dp))
                        .clickable { vm.toggleLookingFor(option) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = option.replace("_", " ").uppercase(),
                        color = if (isOn) Void else Cyan,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))

        SelectField("Availability", p.stringField("availability"),
            state.options.availabilities,
            { vm.onFieldChange("availability", it) })

        SelectField("Preferred Contact", p.stringField("contact_preference"),
            state.options.contactPreferences,
            { vm.onFieldChange("contact_preference", it) })

        Field("Hobbies & Interests", p.stringField("hobbies_and_interests"),
            { vm.onFieldChange("hobbies_and_interests", it) },
            singleLine = false, minLines = 4)
    }
}

@Composable
private fun FavoritesSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Favorites", "The things you'd recommend.") {
        ListEditor("Favorite Movies", p.stringListField("favorite_movies"))
        { vm.onFieldChange("favorite_movies", it) }
        ListEditor("Favorite Books", p.stringListField("favorite_books"))
        { vm.onFieldChange("favorite_books", it) }
        ListEditor("Favorite Songs", p.stringListField("favorite_songs"))
        { vm.onFieldChange("favorite_songs", it) }
        ListEditor("Favorite Shows", p.stringListField("favorite_shows"))
        { vm.onFieldChange("favorite_shows", it) }
        ListEditor("Favorite Games", p.stringListField("favorite_games"))
        { vm.onFieldChange("favorite_games", it) }
        Field("Favorite Quotes", p.stringField("favorite_quotes"),
            { vm.onFieldChange("favorite_quotes", it) }, singleLine = false, minLines = 3)
        Field("Favorite Food", p.stringField("favorite_food"),
            { vm.onFieldChange("favorite_food", it) })
    }
}

@Composable
private fun ListEditor(
    label: String,
    values: List<String>,
    onChange: (List<String>) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Cyan,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(8.dp))

        values.forEachIndexed { index, value ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        val next = values.toMutableList().apply { this[index] = newValue }
                        onChange(next)
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cyan,
                        unfocusedBorderColor = Cyan.copy(alpha = 0.25f),
                        cursorColor = CyanBright,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Blood.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { onChange(values - value) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("×", color = Blood, fontSize = 18.sp)
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Cyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .clickable { onChange(values + "") }
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text("+ Add", color = Cyan, fontSize = 11.sp, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun LinksSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Links", "Everywhere else you can be found.") {

        LinkGroup("Personal") {
            LinkField("Website", "website_url", p, vm, KeyboardType.Uri)
            LinkField("GitHub", "github_url", p, vm, KeyboardType.Uri)
            LinkField("Stack Overflow", "stackoverflow_url", p, vm, KeyboardType.Uri)
            LinkField("Signal", "signal_username", p, vm)
            LinkField("Discord", "discord_handle", p, vm)
            LinkField("PGP Key", "public_pgp_key", p, vm)
        }
        LinkGroup("Social") {
            LinkField("Facebook", "facebook_url", p, vm, KeyboardType.Uri)
            LinkField("Twitter / X", "twitter_url", p, vm, KeyboardType.Uri)
            LinkField("Instagram", "instagram_url", p, vm, KeyboardType.Uri)
            LinkField("TikTok", "tiktok_url", p, vm, KeyboardType.Uri)
            LinkField("Threads", "threads_url", p, vm, KeyboardType.Uri)
            LinkField("LinkedIn", "linkedin_url", p, vm, KeyboardType.Uri)
            LinkField("YouTube", "youtube_url", p, vm, KeyboardType.Uri)
        }
        LinkGroup("Federated") {
            LinkField("Mastodon", "mastodon_url", p, vm, KeyboardType.Uri)
            LinkField("Bluesky", "bluesky_url", p, vm, KeyboardType.Uri)
        }
        LinkGroup("Gaming") {
            LinkField("Steam", "steam_id", p, vm)
            LinkField("PSN", "psn_handle", p, vm)
            LinkField("Xbox", "xbox_gamertag", p, vm)
        }
        LinkGroup("Streaming") {
            LinkField("Twitch", "twitch_url", p, vm, KeyboardType.Uri)
            LinkField("Kick", "kick_url", p, vm, KeyboardType.Uri)
            LinkField("Podcast", "podcast_url", p, vm, KeyboardType.Uri)
        }
        LinkGroup("Security") {
            LinkField("HackerOne", "hackerone_url", p, vm, KeyboardType.Uri)
            LinkField("Bugcrowd", "bugcrowd_url", p, vm, KeyboardType.Uri)
            LinkField("Intigriti", "intigriti_url", p, vm, KeyboardType.Uri)
            LinkField("YesWeHack", "yeswehack_url", p, vm, KeyboardType.Uri)
        }
    }
}

@Composable
private fun LinkGroup(title: String, content: @Composable () -> Unit) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Gold,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
    )
    content()
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun LinkField(
    label: String,
    key: String,
    profile: kotlinx.serialization.json.JsonObject,
    vm: ProfileViewModel,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Field(
        label = label,
        value = profile.stringField(key),
        onChange = { vm.onFieldChange(key, it) },
        keyboardType = keyboardType,
    )
}

@Composable
private fun ThemeSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Theme", "Customize your profile's look.") {
        Field("Accent Color", p.stringField("accent_color"),
            { vm.onFieldChange("accent_color", it) }, "#00e5ff")
        Field("Border Color", p.stringField("border_color"),
            { vm.onFieldChange("border_color", it) }, "#00e5ff")

        SelectField("Theme Preference", p.stringField("theme_preference"),
            listOf("dark", "light", "auto"),
            { vm.onFieldChange("theme_preference", it) })

        SelectField("Border Style", p.stringField("border_style"),
            state.options.borderStyles,
            { vm.onFieldChange("border_style", it) })

        SelectField("Heading Font", p.stringField("font_heading"),
            state.options.fontsHeading,
            { vm.onFieldChange("font_heading", it) })

        SelectField("Body Font", p.stringField("font_body"),
            state.options.fontsBody,
            { vm.onFieldChange("font_body", it) })

        SelectField("Mono Font", p.stringField("font_mono"),
            state.options.fontsMono,
            { vm.onFieldChange("font_mono", it) })

        Field("Wallpaper Opacity (0-100)", p.stringField("wallpaper_opacity"),
            { vm.onFieldChange("wallpaper_opacity", it.toIntOrNull() ?: 50) },
            keyboardType = KeyboardType.Number)
        Field("Wallpaper Blur (0-40)", p.stringField("wallpaper_blur"),
            { vm.onFieldChange("wallpaper_blur", it.toIntOrNull() ?: 0) },
            keyboardType = KeyboardType.Number)
        Field("Border Thickness (0-8)", p.stringField("border_thickness"),
            { vm.onFieldChange("border_thickness", it.toIntOrNull() ?: 1) },
            keyboardType = KeyboardType.Number)
        Field("Music Video ID", p.stringField("music_video_id"),
            { vm.onFieldChange("music_video_id", it) })
    }
}

@Composable
private fun PrivacySection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    SectionScaffold("Privacy", "Who can see what.") {

        SelectField(
            "Profile Visibility",
            p.stringField("visibility").ifBlank { "public" },
            listOf("public", "connections_only", "hidden"),
            { vm.onFieldChange("visibility", it) },
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = "SHOW ON PROFILE",
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(12.dp))

        ToggleRow("Email address", "show_email", p, vm)
        ToggleRow("Phone number", "show_phone", p, vm)
        ToggleRow("Location", "show_location", p, vm)
        ToggleRow("Birthday", "show_birthday", p, vm)
        ToggleRow("Real name", "show_real_name", p, vm)
        ToggleRow("Social links", "show_social_links", p, vm)
        ToggleRow("Premium badge", "show_premium", p, vm)
    }
}

@Composable
private fun ToggleRow(
    label: String,
    key: String,
    profile: kotlinx.serialization.json.JsonObject,
    vm: ProfileViewModel,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = profile.boolField(key),
            onCheckedChange = { vm.onFieldChange(key, it) },
            colors = CheckboxDefaults.colors(
                checkedColor = Cyan,
                uncheckedColor = Cyan.copy(alpha = 0.4f),
                checkmarkColor = Void,
            ),
        )
        Text(text = label, color = TextDim, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun AccountSection(state: ProfileEditState, vm: ProfileViewModel) {
    val p = state.profile
    var username by remember(p) { mutableStateOf(p.stringField("username")) }
    var email    by remember(p) { mutableStateOf(p.stringField("email")) }
    var currentPw by remember { mutableStateOf("") }
    var newPw by remember { mutableStateOf("") }
    var confirmPw by remember { mutableStateOf("") }

    val usernameDirty = username != p.stringField("username") && username.isNotBlank()
    val emailDirty    = email != p.stringField("email") && email.isNotBlank()
    val passwordDirty = currentPw.isNotBlank() && newPw.length >= 12 && newPw == confirmPw

    SectionScaffold("Account", "Sensitive changes require your current password.") {

        Field("Username", username, { username = it })
        CitadelButton(
            text = "Save Username",
            onClick = { vm.updateAccount(username = username) },
            style = CitadelButtonStyle.Cyan,
            enabled = usernameDirty,
        )
        Spacer(Modifier.height(20.dp))

        Field("Email Address", email, { email = it },
            keyboardType = KeyboardType.Email)
        CitadelButton(
            text = "Save Email",
            onClick = { vm.updateAccount(email = email, currentPassword = currentPw) },
            style = CitadelButtonStyle.Cyan,
            enabled = emailDirty && currentPw.isNotBlank(),
        )
        Spacer(Modifier.height(20.dp))

        Text(
            text = "CHANGE PASSWORD",
            style = MaterialTheme.typography.labelSmall,
            color = Gold,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(12.dp))

        Field("Current Password", currentPw, { currentPw = it }, keyboardType = KeyboardType.Password)
        Field("New Password", newPw, { newPw = it }, keyboardType = KeyboardType.Password)
        Field("Confirm New Password", confirmPw, { confirmPw = it }, keyboardType = KeyboardType.Password)

        CitadelButton(
            text = "Change Password",
            onClick = {
                vm.updateAccount(newPassword = newPw, currentPassword = currentPw)
                currentPw = ""; newPw = ""; confirmPw = ""
            },
            style = CitadelButtonStyle.Danger,
            enabled = passwordDirty,
        )
    }
}

/* ══════════════════════════════════════════════════════════════════
 * SELECT FIELD
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun SelectField(
    label: String,
    current: String,
    options: List<String>,
    onSelect: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Cyan,
            letterSpacing = 1.sp,
            fontSize = 10.sp,
        )
        Spacer(Modifier.height(8.dp))
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            options.forEach { option ->
                val isOn = option == current
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isOn) Cyan else Color.Transparent)
                        .border(1.dp, Cyan.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .clickable { onSelect(option) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = option.replace("_", " "),
                        color = if (isOn) Void else Cyan,
                        fontSize = 11.sp,
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

/* ══════════════════════════════════════════════════════════════════
 * SAVE STATUS BANNER
 * ════════════════════════════════════════════════════════════════ */

@Composable
private fun SaveStatusBanner(status: SaveStatus, errorMessage: String?) {
    val visible = status != SaveStatus.Idle
    if (!visible) return

    val (bg, fg, text) = when (status) {
        SaveStatus.Saving -> Triple(Cyan.copy(alpha = 0.15f), Cyan, "Saving…")
        SaveStatus.Saved  -> Triple(Success.copy(alpha = 0.15f), Success, "Saved ✓")
        SaveStatus.Error  -> Triple(Blood.copy(alpha = 0.15f), Blood, errorMessage ?: "Save failed")
        SaveStatus.Idle   -> Triple(Color.Transparent, Color.Transparent, "")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(bg)
                .border(1.dp, fg.copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = text,
                color = fg,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
            )
        }
    }
}