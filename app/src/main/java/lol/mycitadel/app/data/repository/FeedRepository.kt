package lol.mycitadel.app.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import lol.mycitadel.app.data.network.*
import java.io.IOException

/**
 * Repository for feed operations:
 *   • Fetch timeline (all / self / connections) with cursor pagination
 *   • Create post (with optional attachment tokens)
 *   • Update post (content and/or visibility)
 *   • Delete post (soft delete, reputation deduction server-side)
 *   • Upload media attachments via multipart
 */
class FeedRepository(
    private val api: CitadelApi,
    private val appContext: Context,
) {

    private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

    /* ── Result types ─────────────────────────────────────────── */

    sealed interface FetchResult {
        data class Success(
            val posts: List<PostDto>,
            val hasMore: Boolean,
            val nextCursor: String?,
        ) : FetchResult
        data class Failure(val code: String, val message: String) : FetchResult
    }

    sealed interface PostResult {
        data class Success(val postId: Int = 0, val post: PostDto? = null) : PostResult
        data class Failure(val code: String, val message: String) : PostResult
    }

    sealed interface UploadResult {
        data class Success(val attachment: AttachmentDto) : UploadResult
        data class Failure(val code: String, val message: String) : UploadResult
    }

    /* ── Feed fetch ──────────────────────────────────────────── */

    suspend fun fetchFeed(
        scope: String = "all",
        cursor: String? = null,
        limit: Int = 20,
    ): FetchResult = withContext(Dispatchers.IO) {
        try {
            val response = api.feed(scope = scope, limit = limit, cursor = cursor)

            if (!response.isSuccessful) {
                val raw = response.errorBody()?.string().orEmpty()
                return@withContext FetchResult.Failure(
                    parseErrorCode(response.code(), raw),
                    parseErrorMessage(response.code(), raw),
                )
            }

            val body = response.body()
            if (body?.status != "ok") {
                return@withContext FetchResult.Failure("malformed", "Unexpected response.")
            }

            FetchResult.Success(
                posts = body.posts,
                hasMore = body.hasMore,
                nextCursor = body.nextCursor,
            )
        } catch (e: IOException) {
            FetchResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            FetchResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    /* ── Create ───────────────────────────────────────────────── */

    suspend fun createPost(
        content: String,
        visibility: String,
        attachmentTokens: List<String>,
    ): PostResult = withContext(Dispatchers.IO) {
        try {
            val response = api.createPost(
                CreatePostRequest(
                    content = content,
                    visibility = visibility,
                    attachmentTokens = attachmentTokens,
                )
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == "ok") {
                    return@withContext PostResult.Success(postId = body.postId)
                }
            }

            val raw = response.errorBody()?.string().orEmpty()
            PostResult.Failure(
                parseErrorCode(response.code(), raw),
                parseErrorMessage(response.code(), raw),
            )
        } catch (e: IOException) {
            PostResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            PostResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    /* ── Update ───────────────────────────────────────────────── */

    suspend fun updatePost(
        id: Int,
        content: String?,
        visibility: String?,
    ): PostResult = withContext(Dispatchers.IO) {
        try {
            val response = api.updatePost(
                UpdatePostRequest(id = id, content = content, visibility = visibility)
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == "ok") {
                    return@withContext PostResult.Success(post = body.post)
                }
            }

            val raw = response.errorBody()?.string().orEmpty()
            PostResult.Failure(
                parseErrorCode(response.code(), raw),
                parseErrorMessage(response.code(), raw),
            )
        } catch (e: IOException) {
            PostResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            PostResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    /* ── Delete ───────────────────────────────────────────────── */

    suspend fun deletePost(id: Int): PostResult = withContext(Dispatchers.IO) {
        try {
            val response = api.deletePost(DeletePostRequest(id = id))
            if (response.isSuccessful && response.body()?.status == "ok") {
                return@withContext PostResult.Success(postId = id)
            }
            val raw = response.errorBody()?.string().orEmpty()
            PostResult.Failure(
                parseErrorCode(response.code(), raw),
                parseErrorMessage(response.code(), raw),
            )
        } catch (e: IOException) {
            PostResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            PostResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    /* ── Media upload ─────────────────────────────────────────── */

    suspend fun uploadMedia(uri: Uri): UploadResult = withContext(Dispatchers.IO) {
        val part = uriToMultipartPart(uri)
            ?: return@withContext UploadResult.Failure(
                "read_failed",
                "Could not read the selected file."
            )

        try {
            val response = api.uploadMedia(part)

            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.token
                val url = body?.url
                if (body?.status == "ok" && token != null && url != null) {
                    return@withContext UploadResult.Success(
                        AttachmentDto(
                            token = token,
                            url = url,
                            kind = body.kind ?: "document",
                            mime = body.mime ?: "application/octet-stream",
                            name = body.name ?: "",
                            size = body.size,
                            width = body.width,
                            height = body.height,
                        )
                    )
                }
            }

            val raw = response.errorBody()?.string().orEmpty()
            UploadResult.Failure(
                parseErrorCode(response.code(), raw),
                parseErrorMessage(response.code(), raw),
            )
        } catch (e: IOException) {
            UploadResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            UploadResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    /* ── Helpers ──────────────────────────────────────────────── */

    private fun uriToMultipartPart(uri: Uri): MultipartBody.Part? {
        return try {
            val resolver = appContext.contentResolver
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null

            // 50 MB hard cap — matches the API's ceiling
            if (bytes.size > 50 * 1024 * 1024) return null

            val mime = resolver.getType(uri) ?: "application/octet-stream"
            val ext = when (mime) {
                "image/jpeg"      -> "jpg"
                "image/png"       -> "png"
                "image/webp"      -> "webp"
                "image/gif"       -> "gif"
                "video/mp4"       -> "mp4"
                "video/webm"      -> "webm"
                "audio/mpeg"      -> "mp3"
                "audio/ogg"       -> "ogg"
                "audio/wav"       -> "wav"
                "application/pdf" -> "pdf"
                "application/zip" -> "zip"
                "text/plain"      -> "txt"
                "text/markdown"   -> "md"
                else              -> "bin"
            }

            val requestBody = bytes.toRequestBody(mime.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", "upload.$ext", requestBody)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseErrorCode(httpCode: Int, raw: String): String {
        return try {
            errorJson.decodeFromString<ApiError>(raw).code
        } catch (_: Exception) {
            "http_$httpCode"
        }
    }

    private fun parseErrorMessage(httpCode: Int, raw: String): String {
        return try {
            errorJson.decodeFromString<ApiError>(raw).message
        } catch (_: Exception) {
            "Request failed ($httpCode)."
        }
    }
}