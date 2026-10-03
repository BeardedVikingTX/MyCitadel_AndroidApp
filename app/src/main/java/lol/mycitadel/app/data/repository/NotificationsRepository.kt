package lol.mycitadel.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import lol.mycitadel.app.data.network.ApiError
import lol.mycitadel.app.data.network.CitadelApi
import lol.mycitadel.app.data.network.DeleteNotificationRequest
import lol.mycitadel.app.data.network.MarkNotificationReadRequest
import lol.mycitadel.app.data.network.NotificationDto
import lol.mycitadel.app.data.network.RegisterDeviceTokenRequest
import java.io.IOException
class NotificationsRepository(private val api: CitadelApi) {

    private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

    sealed interface ListResult {
        data class Success(
            val notifications: List<NotificationDto>,
            val unreadCount: Int,
        ) : ListResult
        data class Failure(val code: String, val message: String) : ListResult
    }

    sealed interface ActionResult {
        data object Success : ActionResult
        data class Failure(val code: String, val message: String) : ActionResult
    }

    suspend fun list(unreadOnly: Boolean): ListResult = withContext(Dispatchers.IO) {
        try {
            val res = api.notificationsList(
                unreadOnly = if (unreadOnly) 1 else null,
                limit = 50,
            )
            if (!res.isSuccessful) {
                val raw = res.errorBody()?.string().orEmpty()
                return@withContext ListResult.Failure(
                    parseCode(res.code(), raw),
                    parseMessage(res.code(), raw),
                )
            }
            val body = res.body()
            if (body?.status != "ok") {
                return@withContext ListResult.Failure("malformed", "Unexpected response.")
            }
            ListResult.Success(body.notifications, body.unreadCount)
        } catch (e: IOException) {
            ListResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            ListResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    suspend fun markRead(id: Int): ActionResult = withContext(Dispatchers.IO) {
        try {
            val res = api.markNotificationRead(MarkNotificationReadRequest(id = id))
            if (res.isSuccessful && res.body()?.status == "ok") {
                ActionResult.Success
            } else {
                val raw = res.errorBody()?.string().orEmpty()
                ActionResult.Failure(parseCode(res.code(), raw), parseMessage(res.code(), raw))
            }
        } catch (e: IOException) {
            ActionResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    suspend fun markAllRead(): ActionResult = withContext(Dispatchers.IO) {
        try {
            val res = api.markNotificationRead(MarkNotificationReadRequest(all = true))
            if (res.isSuccessful && res.body()?.status == "ok") {
                ActionResult.Success
            } else {
                val raw = res.errorBody()?.string().orEmpty()
                ActionResult.Failure(parseCode(res.code(), raw), parseMessage(res.code(), raw))
            }
        } catch (e: IOException) {
            ActionResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    suspend fun delete(id: Int): ActionResult = withContext(Dispatchers.IO) {
        try {
            val res = api.deleteNotification(DeleteNotificationRequest(id = id))
            if (res.isSuccessful && res.body()?.status == "ok") {
                ActionResult.Success
            } else {
                val raw = res.errorBody()?.string().orEmpty()
                ActionResult.Failure(parseCode(res.code(), raw), parseMessage(res.code(), raw))
            }
        } catch (e: IOException) {
            ActionResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        }
    }

    suspend fun registerDeviceToken(token: String): ActionResult = withContext(Dispatchers.IO) {
        try {
            val res = api.registerDeviceToken(RegisterDeviceTokenRequest(token = token))
            if (res.isSuccessful) {
                ActionResult.Success
            } else {
                val raw = res.errorBody()?.string().orEmpty()
                ActionResult.Failure(parseCode(res.code(), raw), parseMessage(res.code(), raw))
            }
        } catch (e: Exception) {
            ActionResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    private fun parseCode(httpCode: Int, raw: String): String = try {
        errorJson.decodeFromString<ApiError>(raw).code
    } catch (_: Exception) { "http_$httpCode" }

    private fun parseMessage(httpCode: Int, raw: String): String = try {
        errorJson.decodeFromString<ApiError>(raw).message
    } catch (_: Exception) { "Request failed ($httpCode)." }
}