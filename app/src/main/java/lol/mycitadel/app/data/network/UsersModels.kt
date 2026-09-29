package lol.mycitadel.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* ── Directory list ────────────────────────────────────────────── */

@Serializable
data class UsersListResponse(
    val status: String,
    val users: List<UserSummary> = emptyList(),
    val total: Int = 0,
    val limit: Int = 24,
    val offset: Int = 0,
    @SerialName("has_more") val hasMore: Boolean = false,
    val q: String? = null,
)

@Serializable
data class UserSummary(
    val id: Int,
    val username: String,
    @SerialName("display_name") val displayName: String? = null,
    val tagline: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    @SerialName("wallpaper_url") val wallpaperUrl: String? = null,
    @SerialName("accent_color") val accentColor: String? = null,
    @SerialName("country_code") val countryCode: String? = null,
    @SerialName("state_code") val stateCode: String? = null,
    val reputation: Int = 0,
    @SerialName("badge_count") val badgeCount: Int = 0,
    @SerialName("member_since") val memberSince: String? = null,
    @SerialName("connection_state") val connectionState: String = "none",
)

/* ── Single profile view ───────────────────────────────────────── */

@Serializable
data class ProfileViewResponse(
    val status: String,
    val profile: ProfileFull? = null,
    val view: String = "full",      // "full" | "limited"
)

@Serializable
data class ProfileFull(
    val id: Int,
    val username: String,
    @SerialName("display_name") val displayName: String? = null,
    val tagline: String? = null,
    val bio: String? = null,
    val pronouns: String? = null,
    @SerialName("personal_motto") val personalMotto: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    @SerialName("wallpaper_url") val wallpaperUrl: String? = null,
    @SerialName("accent_color") val accentColor: String? = null,
    val reputation: Int = 0,
    @SerialName("badge_count") val badgeCount: Int = 0,
    @SerialName("post_count") val postCount: Int = 0,
    @SerialName("connection_count") val connectionCount: Int = 0,
    @SerialName("member_since") val memberSince: String? = null,
    @SerialName("connection_state") val connectionState: String = "none",
    @SerialName("is_self") val isSelf: Boolean = false,
    @SerialName("is_premium") val isPremium: Boolean = false,
    val email: String? = null,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    val visibility: String? = null,
    val location: ProfileLocation? = null,
    val work: ProfileWork? = null,
    val personal: ProfilePersonal? = null,
    val interests: ProfileInterests? = null,
    val favorites: ProfileFavorites? = null,
    val links: ProfileLinks? = null,
    val theme: ProfileTheme? = null,
    val badges: List<ProfileBadge> = emptyList(),
)

@Serializable
data class ProfileLocation(
    @SerialName("country_code") val countryCode: String? = null,
    @SerialName("state_code") val stateCode: String? = null,
    val timezone: String? = null,
)

@Serializable
data class ProfileWork(
    @SerialName("job_title") val jobTitle: String? = null,
    val company: String? = null,
    @SerialName("years_at_company") val yearsAtCompany: Int? = null,
    val description: String? = null,
    val industry: String? = null,
    val education: String? = null,
)

@Serializable
data class ProfilePersonal(
    @SerialName("relationship_status") val relationshipStatus: String? = null,
    @SerialName("has_kids") val hasKids: Boolean? = null,
    @SerialName("kids_count") val kidsCount: Int? = null,
    @SerialName("languages_spoken") val languagesSpoken: String? = null,
    @SerialName("personality_type") val personalityType: String? = null,
    @SerialName("zodiac_sign") val zodiacSign: String? = null,
    val availability: String? = null,
    @SerialName("contact_preference") val contactPreference: String? = null,
)

@Serializable
data class ProfileInterests(
    val hobbies: String? = null,
    @SerialName("looking_for") val lookingFor: List<String> = emptyList(),
)

@Serializable
data class ProfileFavorites(
    val movies: List<String> = emptyList(),
    val books: List<String> = emptyList(),
    val songs: List<String> = emptyList(),
    val shows: List<String> = emptyList(),
    val games: List<String> = emptyList(),
    val quotes: String? = null,
    val food: String? = null,
)

@Serializable
data class ProfileLinks(
    val website: String? = null,
    val facebook: String? = null,
    val twitter: String? = null,
    val instagram: String? = null,
    val tiktok: String? = null,
    val linkedin: String? = null,
    val youtube: String? = null,
    val threads: String? = null,
    val mastodon: String? = null,
    val bluesky: String? = null,
    val discord: String? = null,
    val steam: String? = null,
    val psn: String? = null,
    val xbox: String? = null,
    val kick: String? = null,
    val twitch: String? = null,
    val podcast: String? = null,
    val github: String? = null,
    val stackoverflow: String? = null,
    val hackerone: String? = null,
    val bugcrowd: String? = null,
    val intigriti: String? = null,
    val yeswehack: String? = null,
    val signal: String? = null,
    @SerialName("pgp_key") val pgpKey: String? = null,
) {
    /** Flat list of (label, value) for non-empty links, ready to render. */
    fun entries(): List<Pair<String, String>> = listOfNotNull(
        website?.takeIf { it.isNotBlank() }?.let { "Website" to it },
        github?.takeIf { it.isNotBlank() }?.let { "GitHub" to it },
        stackoverflow?.takeIf { it.isNotBlank() }?.let { "Stack Overflow" to it },
        facebook?.takeIf { it.isNotBlank() }?.let { "Facebook" to it },
        twitter?.takeIf { it.isNotBlank() }?.let { "Twitter / X" to it },
        instagram?.takeIf { it.isNotBlank() }?.let { "Instagram" to it },
        tiktok?.takeIf { it.isNotBlank() }?.let { "TikTok" to it },
        threads?.takeIf { it.isNotBlank() }?.let { "Threads" to it },
        linkedin?.takeIf { it.isNotBlank() }?.let { "LinkedIn" to it },
        youtube?.takeIf { it.isNotBlank() }?.let { "YouTube" to it },
        mastodon?.takeIf { it.isNotBlank() }?.let { "Mastodon" to it },
        bluesky?.takeIf { it.isNotBlank() }?.let { "Bluesky" to it },
        steam?.takeIf { it.isNotBlank() }?.let { "Steam" to it },
        psn?.takeIf { it.isNotBlank() }?.let { "PSN" to it },
        xbox?.takeIf { it.isNotBlank() }?.let { "Xbox" to it },
        twitch?.takeIf { it.isNotBlank() }?.let { "Twitch" to it },
        kick?.takeIf { it.isNotBlank() }?.let { "Kick" to it },
        podcast?.takeIf { it.isNotBlank() }?.let { "Podcast" to it },
        hackerone?.takeIf { it.isNotBlank() }?.let { "HackerOne" to it },
        bugcrowd?.takeIf { it.isNotBlank() }?.let { "Bugcrowd" to it },
        intigriti?.takeIf { it.isNotBlank() }?.let { "Intigriti" to it },
        yeswehack?.takeIf { it.isNotBlank() }?.let { "YesWeHack" to it },
        discord?.takeIf { it.isNotBlank() }?.let { "Discord" to it },
        signal?.takeIf { it.isNotBlank() }?.let { "Signal" to it },
    )
}

@Serializable
data class ProfileTheme(
    @SerialName("wallpaper_opacity") val wallpaperOpacity: Int = 85,
    @SerialName("wallpaper_blur") val wallpaperBlur: Int = 0,
    @SerialName("accent_color") val accentColor: String? = null,
    @SerialName("theme_preference") val themePreference: String? = null,
    @SerialName("music_video_id") val musicVideoId: String? = null,
    @SerialName("music_autoplay") val musicAutoplay: Boolean = false,
)

@Serializable
data class ProfileBadge(
    val slug: String = "",
    val name: String = "",
    val description: String = "",
    val tier: String = "bronze",
    val color: String = "#00e5ff",
    @SerialName("earned_at") val earnedAt: String? = null,
    @SerialName("is_featured") val isFeatured: Boolean = false,
)

/* ── Connection actions ─────────────────────────────────────────── */

@Serializable
data class ConnectionRequest(
    @SerialName("user_id") val userId: Int,
    val message: String? = null,
)
@Serializable
data class ConnectionRequestResponse(
    val status: String,
    val state: String = "pending",
    val message: String? = null,
)

@Serializable
data class ConnectionActionResponse(
    val status: String,
    val state: String = "",
    val severed: Boolean = false,
    val denied: Boolean = false,
    @SerialName("reputation_earned") val reputationEarned: Int = 0,
    val message: String? = null,
)