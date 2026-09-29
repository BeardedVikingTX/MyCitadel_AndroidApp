package lol.mycitadel.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import lol.mycitadel.app.data.network.ApiError
import lol.mycitadel.app.data.network.CitadelApi
import lol.mycitadel.app.data.network.ConnectionRequest
import lol.mycitadel.app.data.network.ProfileFull
import lol.mycitadel.app.data.network.UserSummary
import java.io.IOException

class UsersRepository(private val api: CitadelApi) {

    private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

    sealed interface ListResult {
        data class Success(
            val users: List<UserSummary>,
            val total: Int,
            val hasMore: Boolean,
        ) : ListResult
        data class Failure(val code: String, val message: String) : ListResult
    }

    sealed interface ProfileResult {
        data class Success(
            val profile: ProfileFull,
            val view: String,           // "full" | "limited"
        ) : ProfileResult
        /** 404 — hidden, blocked, banned, or non-existent. Indistinguishable. */
        data object NotFound : ProfileResult
        data class Failure(val code: String, val message: String) : ProfileResult
    }

    sealed interface ActionResult {
        data class Success(val message: String = "", val reputationEarned: Int = 0) : ActionResult
        data class Failure(val code: String, val message: String) : ActionResult
    }

    /* ── Directory list ─────────────────────────────────────── */

    suspend fun list(
        query: String?,
        offset: Int,
        excludeConnected: Boolean,
    ): ListResult = withContext(Dispatchers.IO) {
        try {
            val response = api.usersList(
                q = query?.takeIf { it.isNotBlank() },
                limit = 24,
                offset = offset,
                excludeConnected = if (excludeConnected) 1 else null,
            )
            if (!response.isSuccessful) {
                val raw = response.errorBody()?.string().orEmpty()
                return@withContext ListResult.Failure(
                    parseCode(response.code(), raw),
                    parseMessage(response.code(), raw),
                )
            }
            val body = response.body()
            if (body?.status != "ok") {
                return@withContext ListResult.Failure("malformed", "Unexpected response.")
            }
            ListResult.Success(
                users = body.users,
                total = body.total,
                hasMore = body.hasMore,
            )
        } catch (e: IOException) {
            ListResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            ListResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    /* ── Single profile ─────────────────────────────────────── */

    suspend fun view(userId: Int): ProfileResult = withContext(Dispatchers.IO) {
        try {
            val response = api.userView(userId)

            if (response.code() == 404) return@withContext ProfileResult.NotFound

            if (!response.isSuccessful) {
                val raw = response.errorBody()?.string().orEmpty()
                return@withContext ProfileResult.Failure(
                    parseCode(response.code(), raw),
                    parseMessage(response.code(), raw),
                )
            }

            val body = response.body()
            val profile = body?.profile
            if (body?.status != "ok" || profile == null) {
                return@withContext ProfileResult.NotFound
            }

            ProfileResult.Success(profile = profile, view = body.view)
        } catch (e: IOException) {
            ProfileResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            ProfileResult.Failure("network_error", e.message ?: "unknown")
        }
    }

    /* ── Connection actions ─────────────────────────────────── */

    suspend fun requestConnection(userId: Int): ActionResult = withContext(Dispatchers.IO) {
        actionCall { api.connectionRequest(ConnectionRequest(userId = userId)) }
    }

    suspend fun acceptConnection(userId: Int): ActionResult = withContext(Dispatchers.IO) {
        actionCall { api.connectionAccept(ConnectionRequest(userId = userId)) }
    }

    suspend fun blockUser(userId: Int): ActionResult = withContext(Dispatchers.IO) {
        actionCall { api.connectionBlock(ConnectionRequest(userId = userId)) }
    }

    private suspend fun actionCall(
        call: suspend () -> retrofit2.Response<out Any>
    ): ActionResult = try {
        val response = call()
        if (response.isSuccessful) {
            // Response body varies by endpoint — we only care about status.
            ActionResult.Success()
        } else {
            val raw = response.errorBody()?.string().orEmpty()
            ActionResult.Failure(
                parseCode(response.code(), raw),
                parseMessage(response.code(), raw),
            )
        }
    } catch (e: IOException) {
        ActionResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
    } catch (e: Exception) {
        ActionResult.Failure("network_error", e.message ?: "unknown")
    }

    /* ── Error helpers ──────────────────────────────────────── */

    private fun parseCode(httpCode: Int, raw: String): String = try {
        errorJson.decodeFromString<ApiError>(raw).code
    } catch (_: Exception) { "http_$httpCode" }

    private fun parseMessage(httpCode: Int, raw: String): String = try {
        errorJson.decodeFromString<ApiError>(raw).message
    } catch (_: Exception) { "Request failed ($httpCode)." }
}