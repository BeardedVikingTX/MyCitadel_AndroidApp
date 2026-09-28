package lol.mycitadel.app.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CitadelApi {

    @GET("auth/csrf.php")
    suspend fun csrf(): Response<CsrfResponse>

    @POST("auth/register.php")
    suspend fun register(@Body body: RegisterRequest): Response<RegisterResponse>

    @POST("auth/logout.php")
    suspend fun logout(@Body body: EmptyRequest = EmptyRequest()): Response<BasicResponse>

    @GET("users/me.php")
    suspend fun me(): Response<MeResponse>

    @GET("users/dashboard.php")
    suspend fun dashboard(): Response<DashboardResponse>

    @POST("auth/login.php")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("auth/login_2fa.php")
    suspend fun login2fa(@Body body: Login2faRequest): Response<Login2faResponse>
}