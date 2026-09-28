package lol.mycitadel.app.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import lol.mycitadel.app.data.network.AccountUpdateRequest
import lol.mycitadel.app.data.network.ApiError
import lol.mycitadel.app.data.network.CitadelApi
import lol.mycitadel.app.data.network.ProfileOptionsDto
import lol.mycitadel.app.data.network.UserDto

/**
 * Repository for profile editor operations.
 * Handles fetch, partial update, image upload, and account changes.
 */
class ProfileRepository(
    private val api: CitadelApi,
    private val appContext: Context,
) {

    private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

    sealed interface FetchResult {
        data class Success(
            val profile: JsonObject,
            val options: ProfileOptionsDto,
        ) : FetchResult

        data class Failure(val code: String, val message: String) : FetchResult
    }

    sealed interface SaveResult {
        data object Success : SaveResult
        data class Failure(val code: String, val message: String) : SaveResult
    }

    sealed interface UploadResult {
        data class Success(val url: String) : UploadResult
        data class Failure(val code: String, val message: String) : UploadResult
    }

    /* ── Fetch profile ─────────────────────────────────────── */

    suspend fun fetch(): FetchResult = withContext(Dispatchers.IO) {
        try {
            val response = api.profile()
            if (!response.isSuccessful) {
                return@withContext FetchResult.Failure(
                    "http_${response.code()}",
                    "Request failed (${response.code()})."
                )
            }
            val body = response.body()
            val profile = body?.profile
            if (body?.status == "ok" && profile != null) {
                return@withContext FetchResult.Success(
                    profile = profile,
                    options = body.options ?: ProfileOptionsDto(),
                )
            }
            FetchResult.Failure("malformed", "Unexpected response from server.")
        } catch (e: Exception) {
            FetchResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    /* ── Save partial profile fields ───────────────────────── */

    suspend fun update(payload: JsonObject): SaveResult = withContext(Dispatchers.IO) {
        try {
            val response = api.profileUpdate(payload)
            if (response.isSuccessful && response.body()?.status == "ok") {
                return@withContext SaveResult.Success
            }
            val raw = response.errorBody()?.string().orEmpty()
            val parsed = try {
                errorJson.decodeFromString<ApiError>(raw)
            } catch (_: Exception) {
                null
            }
            SaveResult.Failure(
                parsed?.code ?: "http_${response.code()}",
                parsed?.message ?: "Save failed (${response.code()})."
            )
        } catch (e: Exception) {
            SaveResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    /* ── Privacy settings ─────────────────────────────────── */

    suspend fun saveSettings(payload: JsonObject): SaveResult = withContext(Dispatchers.IO) {
        try {
            val response = api.profileSettings(payload)
            if (response.isSuccessful && response.body()?.status == "ok") {
                return@withContext SaveResult.Success
            }
            val raw = response.errorBody()?.string().orEmpty()
            val parsed = try {
                errorJson.decodeFromString<ApiError>(raw)
            } catch (_: Exception) {
                null
            }
            SaveResult.Failure(
                parsed?.code ?: "http_${response.code()}",
                parsed?.message ?: "Settings save failed."
            )
        } catch (e: Exception) {
            SaveResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    /* ── Account updates (username, email, password) ───────── */

    suspend fun updateAccount(request: AccountUpdateRequest): SaveResult =
        withContext(Dispatchers.IO) {
            try {
                val response = api.accountUpdate(request)
                if (response.isSuccessful && response.body()?.status == "ok") {
                    return@withContext SaveResult.Success
                }
                val raw = response.errorBody()?.string().orEmpty()
                val parsed = try {
                    errorJson.decodeFromString<ApiError>(raw)
                } catch (_: Exception) {
                    null
                }
                SaveResult.Failure(
                    parsed?.code ?: "http_${response.code()}",
                    parsed?.message ?: "Account update failed."
                )
            } catch (e: Exception) {
                SaveResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
            }
        }

    /* ── Image uploads ─────────────────────────────────────── */

    suspend fun uploadImage(kind: String, uri: Uri): UploadResult =
        withContext(Dispatchers.IO) {
            try {
                val part = uriToMultipart(uri)
                    ?: return@withContext UploadResult.Failure(
                        "read_failed",
                        "Could not read the selected image."
                    )

                val response = when (kind) {
                    "avatar" -> api.uploadAvatar(part)
                    "banner" -> api.uploadBanner(part)
                    "wallpaper" -> api.uploadWallpaper(part)
                    else -> return@withContext UploadResult.Failure(
                        "invalid_kind",
                        "Unknown image type."
                    )
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "ok" && body.url != null) {
                        return@withContext UploadResult.Success(body.url)
                    }
                }
                val raw = response.errorBody()?.string().orEmpty()
                val parsed = try {
                    errorJson.decodeFromString<ApiError>(raw)
                } catch (_: Exception) {
                    null
                }
                UploadResult.Failure(
                    parsed?.code ?: "http_${response.code()}",
                    parsed?.message ?: "Upload failed (${response.code()})."
                )
            } catch (e: Exception) {
                UploadResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
            }
        }

    /* ── Uri → MultipartBody.Part ──────────────────────────── */
    private fun uriToMultipart(uri: Uri): MultipartBody.Part? {
        return try {
            val resolver = appContext.contentResolver
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return null

            // Client-side 5 MB guard (matches the API)
            if (bytes.size > 5 * 1024 * 1024) return null

            val mimeType = resolver.getType(uri) ?: "image/jpeg"
            val extension = when (mimeType) {
                "image/png" -> "png"
                "image/webp" -> "webp"
                else -> "jpg"
            }

            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", "upload.$extension", requestBody)
        } catch (e: Exception) {
            null
        }
    }
}