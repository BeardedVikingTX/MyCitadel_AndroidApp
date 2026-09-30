package lol.mycitadel.app.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* ══════════════════════════════════════════════════════════════════
 * POSTS
 * ════════════════════════════════════════════════════════════════ */

@Serializable
data class PostDto(
    val id: Int,
    @SerialName("user_id") val userId: Int,
    val content: String = "",
    val visibility: String = "public",
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("is_edited") val isEdited: Boolean = false,
    @SerialName("is_deleted") val isDeleted: Boolean = false,
    @SerialName("reaction_count") val reactionCount: Int = 0,
    @SerialName("comment_count") val commentCount: Int = 0,
    @SerialName("author_username") val authorUsername: String = "",
    @SerialName("author_display_name") val authorDisplayName: String? = null,
    @SerialName("author_avatar_url") val authorAvatarUrl: String? = null,
    @SerialName("author_is_premium") val authorIsPremium: Boolean = false,
    @SerialName("author_accent_color") val authorAccentColor: String? = null,
    @SerialName("viewer_reaction") val viewerReaction: String? = null,
    @SerialName("is_own") val isOwn: Boolean = false,
    val attachments: List<AttachmentDto> = emptyList(),
)

@Serializable
data class AttachmentDto(
    val token: String,
    val url: String,
    val kind: String,        // image | video | audio | document
    val mime: String,
    val name: String = "",
    val size: Long = 0,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Double? = null,
)

@Serializable
data class FeedResponse(
    val status: String,
    @SerialName("request_id") val requestId: String? = null,
    val posts: List<PostDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("next_cursor") val nextCursor: String? = null,
    val scope: String = "all",
    val count: Int = 0,
)

@Serializable
data class CreatePostRequest(
    val content: String,
    val visibility: String,
    @SerialName("attachment_tokens") val attachmentTokens: List<String> = emptyList(),
)

@Serializable
data class CreatePostResponse(
    val status: String,
    @SerialName("post_id") val postId: Int,
    val attachments: Int = 0,
    val message: String? = null,
)

@Serializable
data class UpdatePostRequest(
    val id: Int,
    val content: String? = null,
    val visibility: String? = null,
)

@Serializable
data class UpdatePostResponse(
    val status: String,
    val post: PostDto? = null,
    val message: String? = null,
)

@Serializable
data class DeletePostRequest(val id: Int)

@Serializable
data class DeletePostResponse(
    val status: String,
    val message: String? = null,
    @SerialName("reputation_change") val reputationChange: Int = 0,
)

@Serializable
data class UploadMediaResponse(
    val status: String,
    @SerialName("attachment_id") val attachmentId: Int = 0,
    val token: String? = null,
    val url: String? = null,
    val kind: String? = null,
    val mime: String? = null,
    val size: Long = 0,
    val width: Int? = null,
    val height: Int? = null,
    val name: String? = null,
    val message: String? = null,
)


/* ══════════════════════════════════════════════════════════════════
 * REACTIONS
 * ════════════════════════════════════════════════════════════════ */

@Serializable
data class ToggleReactionRequest(
    @SerialName("target_type") val targetType: String,   // "post" | "comment"
    @SerialName("target_id")   val targetId: Int,
    val reaction: String,                                 // like|dislike|heart|angry
)

@Serializable
data class ToggleReactionResponse(
    val status: String,
    val reaction: String? = null,      // null = reaction was removed
    val count: Int = 0,
    val message: String? = null,
)


/* ══════════════════════════════════════════════════════════════════
 * COMMENTS
 * ------------------------------------------------------------------
 * The API nests author metadata under an `author` object rather than
 * flattening it onto the comment. Field names inside `author` are
 * snake_case (display_name, avatar_url, accent_color).
 * ════════════════════════════════════════════════════════════════ */

@Serializable
data class CommentAuthorDto(
    val id: Int,
    val username: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("avatar_url")   val avatarUrl: String? = null,
    @SerialName("accent_color") val accentColor: String? = null,
)

@Serializable
data class CommentDto(
    val id: Int,
    @SerialName("post_id")   val postId: Int,
    @SerialName("parent_id") val parentId: Int? = null,
    val author: CommentAuthorDto,
    val content: String = "",
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("is_edited")  val isEdited: Boolean = false,
    @SerialName("viewer_reaction")   val viewerReaction: String? = null,
    @SerialName("viewer_can_delete") val viewerCanDelete: Boolean = false,
)

@Serializable
data class CommentsListResponse(
    val status: String,
    val comments: List<CommentDto> = emptyList(),
    val total: Int = 0,
    val limit: Int = 50,
    val offset: Int = 0,
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class CreateCommentRequest(
    @SerialName("post_id")   val postId: Int,
    val content: String,
    @SerialName("parent_id") val parentId: Int? = null,
)

@Serializable
data class CreateCommentResponse(
    val status: String,
    @SerialName("comment_id") val commentId: Int,
    @SerialName("parent_id")  val parentId: Int? = null,
    @SerialName("is_reply")   val isReply: Boolean = false,
    val message: String? = null,
)

@Serializable
data class DeleteCommentRequest(val id: Int)

@Serializable
data class DeleteCommentResponse(
    val status: String,
    val message: String? = null,
    @SerialName("reputation_change") val reputationChange: Int = 0,
)