package lol.mycitadel.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ProfileResponse(
    val status: String,
    val profile: JsonObject? = null,
    val options: ProfileOptionsDto? = null,
)

@Serializable
data class ProfileOptionsDto(
    @SerialName("fonts_heading")          val fontsHeading: List<String> = emptyList(),
    @SerialName("fonts_body")             val fontsBody: List<String> = emptyList(),
    @SerialName("fonts_mono")             val fontsMono: List<String> = emptyList(),
    @SerialName("border_styles")          val borderStyles: List<String> = emptyList(),
    @SerialName("relationship_statuses")  val relationshipStatuses: List<String> = emptyList(),
    @SerialName("availabilities")         val availabilities: List<String> = emptyList(),
    @SerialName("contact_preferences")    val contactPreferences: List<String> = emptyList(),
    @SerialName("looking_for_options")    val lookingForOptions: List<String> = emptyList(),
)

@Serializable
data class UpdateProfileResponse(
    val status: String,
    val updated: List<String> = emptyList(),
    val message: String? = null,
)

@Serializable
data class UploadResponse(
    val status: String,
    val url: String? = null,
    val message: String? = null,
)

@Serializable
data class AccountUpdateRequest(
    val username: String? = null,
    val email: String? = null,
    @SerialName("new_password") val newPassword: String? = null,
    @SerialName("current_password") val currentPassword: String? = null,
)