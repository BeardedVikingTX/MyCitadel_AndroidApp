package lol.mycitadel.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* ── CSRF ──────────────────────────────────────────────────────── */

@Serializable
data class CsrfResponse(
    val status: String,
    @SerialName("request_id") val requestId: String? = null,
    val ts: String? = null,
    val token: String? = null,
    @SerialName("expires_in") val expiresIn: Int? = null
)

/* ── Register ──────────────────────────────────────────────────── */

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerialName("referral_code") val referralCode: String? = null
)

@Serializable
data class RegisterResponse(
    val status: String,
    @SerialName("request_id") val requestId: String? = null,
    val ts: String? = null,
    val user: UserDto? = null,
    @SerialName("csrf_token") val csrfToken: String? = null
)

/* ── User ──────────────────────────────────────────────────────── */

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String? = null,
    val reputation: Int = 0,
    val premium: Boolean = false,
    @SerialName("email_verified") val emailVerified: Boolean = false,
    @SerialName("referral_code") val referralCode: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("last_login_at") val lastLoginAt: String? = null,

    // Profile fields — populated by /users/me.php since the API update
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("avatar_url")   val avatarUrl: String? = null,
    @SerialName("banner_url")   val bannerUrl: String? = null,
    @SerialName("tagline")      val tagline: String? = null,
)
@Serializable
data class MeResponse(
    val status: String,
    val user: UserDto? = null
)

/* ── Generic ───────────────────────────────────────────────────── */

@Serializable
data class EmptyRequest(val placeholder: String? = null)

@Serializable
data class BasicResponse(
    val status: String,
    val message: String? = null,
    @SerialName("request_id") val requestId: String? = null
)

@Serializable
data class ApiError(
    val status: String,
    val code: String,
    val message: String,
    @SerialName("request_id") val requestId: String? = null
)

/* ══════════════════════════════════════════════════════════════════
 * DASHBOARD
 * ════════════════════════════════════════════════════════════════ */

@Serializable
data class DashboardResponse(
    val status: String,
    @SerialName("request_id") val requestId: String? = null,
    val identity: IdentityDto? = null,
    val account: AccountDto? = null,
    val stats: StatsDto? = null,
    val rank: RankDto? = null,
    val badges: List<BadgeDto> = emptyList(),
    val activity: List<ActivityDto> = emptyList(),
    val community: CommunityDto? = null,
    val features: Map<String, Boolean> = emptyMap()
)

@Serializable
data class IdentityDto(
    val id: Int,
    val username: String,
    @SerialName("display_name") val displayName: String? = null,
    val email: String? = null,
    val tagline: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    @SerialName("accent_color") val accentColor: String? = null,
    val theme: String? = null,
    val visibility: String = "public"
)

@Serializable
data class AccountDto(
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("is_banned") val isBanned: Boolean = false,
    @SerialName("email_verified") val emailVerified: Boolean = false,
    @SerialName("is_premium") val isPremium: Boolean = false,
    @SerialName("premium_since") val premiumSince: String? = null,
    @SerialName("member_since") val memberSince: String? = null,
    val age: AgeDto? = null,
    @SerialName("last_login_at") val lastLoginAt: String? = null
)

@Serializable
data class AgeDto(
    val days: Int = 0,
    val weeks: Int = 0,
    val months: Int = 0,
    val years: Int = 0,
    val human: String = "today"
)

@Serializable
data class StatsDto(
    val reputation: Int = 0,
    @SerialName("badge_count") val badgeCount: Int = 0,
    @SerialName("post_count") val postCount: Int = 0,
    @SerialName("comment_given_count") val commentGivenCount: Int = 0,
    @SerialName("comment_received_count") val commentReceivedCount: Int = 0,
    @SerialName("connection_count") val connectionCount: Int = 0,
    @SerialName("checkin_streak") val checkinStreak: Int = 0,
    @SerialName("checkin_longest_streak") val checkinLongestStreak: Int = 0,
    val reactions: ReactionsDto = ReactionsDto(),
    val referrals: ReferralsDto = ReferralsDto()
)

@Serializable
data class ReactionsDto(
    @SerialName("given_like") val givenLike: Int = 0,
    @SerialName("given_heart") val givenHeart: Int = 0,
    @SerialName("recv_like") val recvLike: Int = 0,
    @SerialName("recv_heart") val recvHeart: Int = 0
)

@Serializable
data class ReferralsDto(
    val count: Int = 0,
    val points: Int = 0,
    val code: String? = null
)

@Serializable
data class RankDto(
    val position: Int = 1,
    @SerialName("total_users") val totalUsers: Int = 1,
    val percentile: Int = 100,
    @SerialName("avg_rep") val avgRep: Int = 0,
    @SerialName("your_rep") val yourRep: Int = 0,
    @SerialName("above_avg") val aboveAvg: Boolean = false
)

@Serializable
data class BadgeDto(
    val slug: String,
    val name: String,
    val description: String = "",
    val category: String = "",
    val threshold: Int = 0,
    val tier: String = "bronze",
    @SerialName("icon_svg") val iconSvg: String? = null,
    val color: String = "#00e5ff",
    @SerialName("earned_at") val earnedAt: String? = null,
    @SerialName("is_featured") val isFeatured: Boolean = false
)

@Serializable
data class ActivityDto(
    val delta: Int = 0,
    val reason: String = "",
    @SerialName("ref_type") val refType: String? = null,
    @SerialName("ref_id") val refId: Int? = null,
    val note: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class CommunityDto(
    @SerialName("total_users") val totalUsers: Int = 0,
    @SerialName("average_rep") val averageRep: Int = 0,
    @SerialName("total_rep") val totalRep: Int = 0,
    @SerialName("total_badges") val totalBadges: Int = 0
)
/* ══════════════════════════════════════════════════════════════════
 * LOGIN
 * ════════════════════════════════════════════════════════════════ */

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val status: String,
    @SerialName("request_id") val requestId: String? = null,
    val user: UserDto? = null,
    @SerialName("csrf_token") val csrfToken: String? = null,
    @SerialName("two_fa_required") val twoFaRequired: Boolean = false,
    val message: String? = null,
    @SerialName("expires_in") val expiresIn: Int? = null
)

@Serializable
data class Login2faRequest(
    val code: String
)

@Serializable
data class Login2faResponse(
    val status: String,
    val user: UserDto? = null,
    @SerialName("csrf_token") val csrfToken: String? = null,
    @SerialName("via_recovery_code") val viaRecoveryCode: Boolean = false,
    @SerialName("recovery_codes_remaining") val recoveryCodesRemaining: Int? = null
)