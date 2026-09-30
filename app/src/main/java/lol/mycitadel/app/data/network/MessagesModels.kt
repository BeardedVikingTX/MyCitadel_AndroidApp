package lol.mycitadel.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationDto(
    val id: Long,
    val other: OtherUserDto? = null,
    @SerialName("last_preview") val lastPreview: String? = null,
    @SerialName("last_sender_id") val lastSenderId: Int? = null,
    @SerialName("unread_count") val unreadCount: Int = 0,
    @SerialName("last_message_at") val lastMessageAt: String? = null,
)

@Serializable
data class OtherUserDto(
    val id: Int,
    val username: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class MessageAttachmentDto(
    val token: String,
    val url: String,
    val kind: String,
    val mime: String = "",
    val name: String = "",
    val size: Long = 0,
    val width: Int? = null,
    val height: Int? = null,
)

@Serializable
data class MessageDto(
    val id: Long,
    @SerialName("conversation_id") val conversationId: Long,
    @SerialName("sender_id") val senderId: Int,
    val body: String = "",
    val deleted: Boolean = false,
    val encrypted: Boolean = false,
    val edited: Boolean = false,
    @SerialName("created_at") val createdAt: String,
    val attachments: List<MessageAttachmentDto> = emptyList(),
)

@Serializable
data class ConversationsListResponse(
    val status: String,
    val conversations: List<ConversationDto> = emptyList(),
)

@Serializable
data class ThreadResponse(
    val status: String,
    @SerialName("conversation_id") val conversationId: Long,
    val messages: List<MessageDto> = emptyList(),
    val other: OtherUserDto? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class OpenConversationRequest(@SerialName("to") val to: Int)

@Serializable
data class OpenConversationResponse(
    val status: String,
    @SerialName("conversation_id") val conversationId: Long,
    val created: Boolean = false,
)

@Serializable
data class SendMessageRequest(
    @SerialName("conversation_id") val conversationId: Long? = null,
    @SerialName("to") val to: Int? = null,
    val body: String,
    @SerialName("attachment_tokens") val attachmentTokens: List<String> = emptyList(),
    @SerialName("idempotency_key") val idempotencyKey: String? = null,
)

@Serializable
data class SendMessageResponse(
    val status: String,
    @SerialName("message_id") val messageId: Long,
    @SerialName("conversation_id") val conversationId: Long,
    val duplicate: Boolean = false,
)

@Serializable
data class StreamResponse(
    val status: String,
    val messages: List<MessageDto> = emptyList(),
    @SerialName("last_id") val lastId: Long = 0,
    val timeout: Boolean = false,
)

@Serializable
data class MarkReadRequest(
    @SerialName("conversation_id") val conversationId: Long,
    @SerialName("up_to_message_id") val upToMessageId: Long? = null,
)

@Serializable
data class BasicMessageResponse(
    val status: String,
    val message: String? = null,
)