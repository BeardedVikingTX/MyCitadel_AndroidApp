package lol.mycitadel.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/* ══════════════════════════════════════════════════════════════════
 * NOTIFICATIONS
 * ------------------------------------------------------------------
 * The API nests actor metadata under `actor`. The `payload` field
 * may arrive as a JSON object OR as a JSON string, so it's typed
 * as JsonElement and decoded manually by the consumer.
 * ════════════════════════════════════════════════════════════════ */

@Serializable
data class NotificationActorDto(
    val id: Int,
    val username: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("avatar_url")   val avatarUrl: String? = null,
)

@Serializable
data class NotificationDto(
    val id: Int,
    val type: String,
    val title: String = "",
    val body: String? = null,
    val payload: JsonElement? = null,
    val link: String? = null,
    val read: Boolean = false,
    @SerialName("created_at") val createdAt: String,
    val actor: NotificationActorDto? = null,
)

@Serializable
data class NotificationsListResponse(
    val status: String,
    val notifications: List<NotificationDto> = emptyList(),
    @SerialName("unread_count") val unreadCount: Int = 0,
)

@Serializable
data class MarkNotificationReadRequest(
    val id: Int? = null,
    val all: Boolean? = null,
)

@Serializable
data class MarkReadResponse(
    val status: String,
    val marked: Int = 0,
)

@Serializable
data class DeleteNotificationRequest(val id: Int)

@Serializable
data class DeleteNotificationResponse(
    val status: String,
    val deleted: Int = 0,
)