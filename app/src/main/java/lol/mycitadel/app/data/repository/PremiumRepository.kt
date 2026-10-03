package lol.mycitadel.app.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import lol.mycitadel.app.data.network.ApiError
import lol.mycitadel.app.data.network.CitadelApi
import lol.mycitadel.app.data.network.CitadelClient
import lol.mycitadel.app.data.network.EmptyRequest
import java.io.IOException

class PremiumRepository(
    private val api: CitadelApi,
    private val client: CitadelClient,
) {

    private val errorJson = Json { ignoreUnknownKeys = true; isLenient = true }

    sealed interface CheckoutResult {
        data class Success(val checkoutUrl: String, val sessionId: String?) : CheckoutResult
        data class Failure(val code: String, val message: String) : CheckoutResult
    }

    sealed interface PortalResult {
        data class Success(val portalUrl: String) : PortalResult
        data class Failure(val code: String, val message: String) : PortalResult
    }

    sealed interface StatusResult {
        data class Success(val isPremium: Boolean) : StatusResult
        data class Failure(val code: String, val message: String) : StatusResult
    }

    private suspend fun ensureCsrf() {
        if (client.csrfToken.isNullOrBlank()) {
            try {
                val res = api.csrf()
                res.body()?.token?.let { client.csrfToken = it }
            } catch (_: Exception) { }
        }
    }

    suspend fun createCheckoutSession(): CheckoutResult = withContext(Dispatchers.IO) {
        ensureCsrf()
        try {
            val res = api.premiumCheckout(EmptyRequest())
            val body = res.body()
            if (res.isSuccessful && body?.status == "ok" && !body.checkoutUrl.isNullOrBlank()) {
                CheckoutResult.Success(body.checkoutUrl, body.sessionId)
            } else {
                val raw = res.errorBody()?.string().orEmpty()
                val parsed = try { errorJson.decodeFromString<ApiError>(raw) } catch (_: Exception) { null }
                if (parsed?.code == "csrf_invalid") client.csrfToken = null
                val msg = parsed?.message ?: body?.status ?: "Could not start checkout session (${res.code()})."
                CheckoutResult.Failure(parsed?.code ?: "checkout_failed", msg)
            }
        } catch (e: IOException) {
            CheckoutResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            CheckoutResult.Failure("error", e.message ?: "unknown")
        }
    }

    suspend fun openPortalSession(): PortalResult = withContext(Dispatchers.IO) {
        ensureCsrf()
        try {
            val res = api.premiumPortal(EmptyRequest())
            val body = res.body()
            if (res.isSuccessful && body?.status == "ok" && !body.portalUrl.isNullOrBlank()) {
                PortalResult.Success(body.portalUrl)
            } else {
                val raw = res.errorBody()?.string().orEmpty()
                val parsed = try { errorJson.decodeFromString<ApiError>(raw) } catch (_: Exception) { null }
                if (parsed?.code == "csrf_invalid") client.csrfToken = null
                val msg = parsed?.message ?: body?.status ?: "Could not open billing portal (${res.code()})."
                PortalResult.Failure(parsed?.code ?: "portal_failed", msg)
            }
        } catch (e: IOException) {
            PortalResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            PortalResult.Failure("error", e.message ?: "unknown")
        }
    }

    suspend fun fetchStatus(): StatusResult = withContext(Dispatchers.IO) {
        try {
            val res = api.premiumStatus()
            val body = res.body()
            if (res.isSuccessful && body?.status == "ok") {
                StatusResult.Success(body.premium)
            } else {
                val raw = res.errorBody()?.string().orEmpty()
                val parsed = try { errorJson.decodeFromString<ApiError>(raw) } catch (_: Exception) { null }
                StatusResult.Failure(parsed?.code ?: "status_failed", parsed?.message ?: "Could not fetch status.")
            }
        } catch (e: IOException) {
            StatusResult.Failure("network_error", "Network error: ${e.message ?: "unknown"}")
        } catch (e: Exception) {
            StatusResult.Failure("error", e.message ?: "unknown")
        }
    }
}
