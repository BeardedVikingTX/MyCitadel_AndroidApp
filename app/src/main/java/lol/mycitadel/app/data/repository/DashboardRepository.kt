package lol.mycitadel.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import lol.mycitadel.app.data.network.ApiError
import lol.mycitadel.app.data.network.CitadelApi
import lol.mycitadel.app.data.network.DashboardResponse

class DashboardRepository(private val api: CitadelApi) {

    private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

    sealed interface Result {
        data class Success(val data: DashboardResponse) : Result
        data class Failure(val code: String, val message: String) : Result
    }

    suspend fun fetch(): Result = withContext(Dispatchers.IO) {
        val response = try {
            api.dashboard()
        } catch (e: Exception) {
            return@withContext Result.Failure(
                "network_error",
                "Network error: ${e.message ?: "unknown"}"
            )
        }

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.status == "ok") {
                return@withContext Result.Success(body)
            }
            return@withContext Result.Failure(
                "malformed",
                "Unexpected response from server."
            )
        }

        // 401 = not logged in
        if (response.code() == 401) {
            return@withContext Result.Failure(
                "unauthenticated",
                "Your session has expired. Please log in again."
            )
        }

        val raw = response.errorBody()?.string().orEmpty()
        val parsed = try {
            errorJson.decodeFromString<ApiError>(raw)
        } catch (_: Exception) { null }

        Result.Failure(
            code = parsed?.code ?: "http_${response.code()}",
            message = parsed?.message ?: "Request failed (${response.code()})."
        )
    }
}