package lol.mycitadel.app.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class CitadelClient(cookieJar: PersistentCookieJar) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Volatile so the interceptor sees the latest value immediately
     * after AuthRepository updates it on the IO thread.
     */
    @Volatile
    var csrfToken: String? = null

    private val headerInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder()
            .header("X-Citadel-Client", CLIENT_HEADER)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "application/json")
            .build()
        chain.proceed(req)
    }

    /**
     * Adds the CSRF token to every unsafe request (POST/PUT/PATCH/DELETE).
     * Safe methods (GET/HEAD/OPTIONS) don't need it.
     *
     * If csrfToken is null, the request goes out without the header and
     * the server will respond with csrf_invalid — which is the correct
     * signal that we need to fetch a fresh token.
     */
    private val csrfInterceptor = Interceptor { chain ->
        val original = chain.request()
        val method = original.method.uppercase()
        val isUnsafe = method == "POST" || method == "PUT" ||
                method == "PATCH" || method == "DELETE"

        val request = if (isUnsafe && !csrfToken.isNullOrBlank()) {
            original.newBuilder()
                .header("X-CSRF-Token", csrfToken!!)
                .build()
        } else {
            original
        }

        chain.proceed(request)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(headerInterceptor)
        .addInterceptor(csrfInterceptor)
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    val api: CitadelApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttp)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CitadelApi::class.java)

    companion object {
        const val BASE_URL      = "https://api.mycitadel.lol/v1/"
        const val CLIENT_HEADER = "android/1.0.0"
        const val USER_AGENT    = "MyCitadelAndroid/1.0.0"
    }
}