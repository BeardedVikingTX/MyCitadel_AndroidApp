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

class MessagesRepository(
    private val api: CitadelApi,
    private val appContext: Context,
) {

    private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

    sealed interface Result<out T> {
        data class Success<T>(val data: T) : Result<T>
        data class Failure(val code: String, val message: String) : Result<Nothing>
    }

    /* ── Inbox ──────────────────────────────────────────────── */

    suspend fun listConversations(): Result<List<ConversationDto>> = withContext(Dispatchers.IO) {
        try {
            val res = api.conversations()
            if (!res.isSuccessful) return@withContext parseFailure(res.code(), res.errorBody()?.string())
            val body = res.body()
            if (body?.status != "ok") return@withContext Result.Failure("malformed", "Unexpected response.")
            Result.Success(body.conversations)
        } catch (e: IOException) {
            Result.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    /* ── Thread ─────────────────────────────────────────────── */

    suspend fun loadThread(conversationId: Long): Result<ThreadResponse> = withContext(Dispatchers.IO) {
        try {
            val res = api.messageThread(conversationId = conversationId, limit = 50)
            if (!res.isSuccessful) return@withContext parseFailure(res.code(), res.errorBody()?.string())
            val body = res.body()
            if (body?.status != "ok") return@withContext Result.Failure("malformed", "Unexpected response.")
            Result.Success(body)
        } catch (e: IOException) {
            Result.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    /* ── Stream (long-poll) ─────────────────────────────────── */

    suspend fun stream(since: Long): Result<StreamResponse> = withContext(Dispatchers.IO) {
        try {
            val res = api.messagesStream(since)
            if (!res.isSuccessful) return@withContext parseFailure(res.code(), res.errorBody()?.string())
            val body = res.body()
            if (body?.status != "ok") return@withContext Result.Failure("malformed", "Unexpected response.")
            Result.Success(body)
        } catch (e: IOException) {
            Result.Failure("network_error", e.message ?: "unknown")
        } catch (e: Exception) {
            Result.Failure("network_error", e.message ?: "unknown")
        }
    }

    /* ── Open (find or create) ──────────────────────────────── */

    suspend fun openConversation(targetUserId: Int): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val res = api.openConversation(OpenConversationRequest(to = targetUserId))
            if (!res.isSuccessful) return@withContext parseFailure(res.code(), res.errorBody()?.string())
            val body = res.body()
            if (body?.status != "ok") return@withContext Result.Failure("malformed", "Unexpected response.")
            Result.Success(body.conversationId)
        } catch (e: IOException) {
            Result.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    /* ── Send ───────────────────────────────────────────────── */

    suspend fun send(
        conversationId: Long,
        body: String,
        attachmentTokens: List<String>,
        idempotencyKey: String,
    ): Result<SendMessageResponse> = withContext(Dispatchers.IO) {
        try {
            val res = api.sendMessage(
                SendMessageRequest(
                    conversationId = conversationId,
                    body = body,
                    attachmentTokens = attachmentTokens,
                    idempotencyKey = idempotencyKey,
                )
            )
            if (!res.isSuccessful) return@withContext parseFailure(res.code(), res.errorBody()?.string())
            val data = res.body()
            if (data?.status != "ok") return@withContext Result.Failure("malformed", "Unexpected response.")
            Result.Success(data)
        } catch (e: IOException) {
            Result.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    /* ── Mark read ──────────────────────────────────────────── */

    suspend fun markRead(conversationId: Long, upToMessageId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val res = api.markRead(MarkReadRequest(conversationId, upToMessageId))
            if (!res.isSuccessful) return@withContext parseFailure(res.code(), res.errorBody()?.string())
            Result.Success(Unit)
        } catch (_: Exception) {
            Result.Failure("network_error", "network")
        }
    }

    /* ── Attachment upload ──────────────────────────────────── */

    suspend fun uploadAttachment(uri: Uri): Result<MessageAttachmentDto> = withContext(Dispatchers.IO) {
        val part = uriToPart(uri)
            ?: return@withContext Result.Failure("read_failed", "Could not read the selected file.")

        try {
            val res = api.uploadMedia(part)
            if (!res.isSuccessful) return@withContext parseFailure(res.code(), res.errorBody()?.string())
            val data = res.body()
            if (data?.status != "ok" || data.token == null || data.url == null) {
                return@withContext Result.Failure("malformed", "Unexpected upload response.")
            }
            Result.Success(
                MessageAttachmentDto(
                    token = data.token,
                    url = data.url,
                    kind = data.kind ?: "document",
                    mime = data.mime ?: "",
                    name = data.name ?: "",
                    size = data.size,
                    width = data.width,
                    height = data.height,
                )
            )
        } catch (e: IOException) {
            Result.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    private fun uriToPart(uri: Uri): MultipartBody.Part? {
        return try {
            val resolver = appContext.contentResolver
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
            if (bytes.size > 50 * 1024 * 1024) return null

            val mime = resolver.getType(uri) ?: "application/octet-stream"
            val ext = when (mime) {
                "image/jpeg" -> "jpg"; "image/png" -> "png"; "image/webp" -> "webp"
                "image/gif"  -> "gif"; "video/mp4" -> "mp4"; "video/webm" -> "webm"
                "audio/mpeg" -> "mp3"; "audio/ogg" -> "ogg"; "audio/wav"  -> "wav"
                "application/pdf" -> "pdf"; "application/zip" -> "zip"
                "text/plain" -> "txt"; "text/markdown" -> "md"
                else -> "bin"
            }
            val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", "upload.$ext", body)
        } catch (_: Exception) {
            null
        }
    }

    private fun parseFailure(httpCode: Int, raw: String?): Result.Failure {
        val text = raw.orEmpty()
        val parsed = try { errorJson.decodeFromString<ApiError>(text) } catch (_: Exception) { null }
        return Result.Failure(
            code = parsed?.code ?: "http_$httpCode",
            message = parsed?.message ?: "Request failed ($httpCode).",
        )
    }
}