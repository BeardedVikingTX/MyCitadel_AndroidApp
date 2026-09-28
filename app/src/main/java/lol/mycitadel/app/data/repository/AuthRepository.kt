package lol.mycitadel.app.data.repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lol.mycitadel.app.data.network.UserDto
import kotlinx.serialization.json.Json
import lol.mycitadel.app.data.network.ApiError
import lol.mycitadel.app.data.network.CitadelApi
import lol.mycitadel.app.data.network.CitadelClient
import lol.mycitadel.app.data.network.Login2faRequest
import lol.mycitadel.app.data.network.LoginRequest
import lol.mycitadel.app.data.network.RegisterRequest


class AuthRepository(
    private val api: CitadelApi,
    private val client: CitadelClient,
) {

    private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

    /* ── Result types ─────────────────────────────────────────── */

    sealed interface RegisterResult {
        data class Success(val user: UserDto) : RegisterResult
        data class Failure(val code: String, val message: String) : RegisterResult
    }

    sealed interface LoginResult {
        data class Success(val user: UserDto) : LoginResult
        data class TwoFactorRequired(val message: String) : LoginResult
        data class Failure(val code: String, val message: String) : LoginResult
    }

    /* ── Register ─────────────────────────────────────────────── */

    suspend fun register(
        username: String,
        email: String,
        password: String,
        referralCode: String? = null
    ): RegisterResult = withContext(Dispatchers.IO) {

        if (client.csrfToken.isNullOrBlank()) {
            val token = fetchCsrf()
                ?: return@withContext RegisterResult.Failure(
                    "csrf_failed",
                    "Could not reach the API. Check your connection and try again."
                )
            client.csrfToken = token
        }

        val response = try {
            api.register(
                RegisterRequest(
                    username = username.trim(),
                    email = email.trim(),
                    password = password,
                    referralCode = referralCode?.trim()?.takeIf { it.isNotEmpty() }
                )
            )
        } catch (e: Exception) {
            return@withContext RegisterResult.Failure(
                "network_error",
                "Network error: ${e.message ?: "unknown"}"
            )
        }

        if (response.isSuccessful) {
            val body = response.body()
            val user = body?.user
            if (body?.status == "ok" && user != null) {
                body.csrfToken?.let { client.csrfToken = it }
                return@withContext RegisterResult.Success(user)
            }
            return@withContext RegisterResult.Failure(
                "malformed", "Unexpected response from server."
            )
        }

        val raw = response.errorBody()?.string().orEmpty()
        val parsed = try { errorJson.decodeFromString<ApiError>(raw) } catch (_: Exception) { null }

        if (parsed?.code == "csrf_invalid") client.csrfToken = null

        RegisterResult.Failure(
            code = parsed?.code ?: "http_${response.code()}",
            message = parsed?.message ?: "Request failed (${response.code()})."
        )
    }

    /* ── Login (step 1: identifier + password) ────────────────── */

    suspend fun login(
        identifier: String,
        password: String
    ): LoginResult = withContext(Dispatchers.IO) {

        if (client.csrfToken.isNullOrBlank()) {
            val token = fetchCsrf()
                ?: return@withContext LoginResult.Failure(
                    "csrf_failed",
                    "Could not reach the API. Check your connection and try again."
                )
            client.csrfToken = token
        }

        val response = try {
            api.login(LoginRequest(identifier = identifier.trim(), password = password))
        } catch (e: Exception) {
            return@withContext LoginResult.Failure(
                "network_error",
                "Network error: ${e.message ?: "unknown"}"
            )
        }

        if (response.isSuccessful) {
            val body = response.body()
            body?.csrfToken?.let { client.csrfToken = it }

            // 2FA gate
            if (body?.twoFaRequired == true) {
                return@withContext LoginResult.TwoFactorRequired(
                    body.message ?: "Enter the 6-digit code from your authenticator app."
                )
            }

            val user = body?.user
            if (body?.status == "ok" && user != null) {
                return@withContext LoginResult.Success(user)
            }
            return@withContext LoginResult.Failure(
                "malformed", "Unexpected response from server."
            )
        }

        val raw = response.errorBody()?.string().orEmpty()
        val parsed = try { errorJson.decodeFromString<ApiError>(raw) } catch (_: Exception) { null }

        if (parsed?.code == "csrf_invalid") client.csrfToken = null

        LoginResult.Failure(
            code = parsed?.code ?: "http_${response.code()}",
            message = parsed?.message ?: "Request failed (${response.code()})."
        )
    }

    /* ── Login (step 2: 2FA code) ─────────────────────────────── */

    suspend fun login2fa(code: String): LoginResult = withContext(Dispatchers.IO) {

        val response = try {
            api.login2fa(Login2faRequest(code = code.trim()))
        } catch (e: Exception) {
            return@withContext LoginResult.Failure(
                "network_error",
                "Network error: ${e.message ?: "unknown"}"
            )
        }

        if (response.isSuccessful) {
            val body = response.body()
            body?.csrfToken?.let { client.csrfToken = it }
            val user = body?.user
            if (body?.status == "ok" && user != null) {
                return@withContext LoginResult.Success(user)
            }
            return@withContext LoginResult.Failure(
                "malformed", "Unexpected response from server."
            )
        }

        val raw = response.errorBody()?.string().orEmpty()
        val parsed = try { errorJson.decodeFromString<ApiError>(raw) } catch (_: Exception) { null }

        if (parsed?.code == "csrf_invalid") client.csrfToken = null

        LoginResult.Failure(
            code = parsed?.code ?: "http_${response.code()}",
            message = parsed?.message ?: "Request failed (${response.code()})."
        )
    }

    /* ── CSRF ─────────────────────────────────────────────────── */

    private suspend fun fetchCsrf(): String? = try {
        val res = api.csrf()
        res.body()?.token
    } catch (_: Exception) {
        null
    }

    fun clearCachedCsrf() {
        client.csrfToken = null
    }

    /**
     * Fetch the full current-user profile via /users/me.php.
     * The login/register responses only return the auth shell — this
     * method returns the enriched user with avatar_url, display_name, etc.
     * Returns null on failure (caller falls back to the partial user).
     */
    suspend fun fetchMe(): UserDto? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val response = api.me()
            if (response.isSuccessful) response.body()?.user else null
        } catch (_: Exception) {
            null
        }
    }
}