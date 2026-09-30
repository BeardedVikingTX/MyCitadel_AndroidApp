package lol.mycitadel.app.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import kotlinx.serialization.json.JsonObject
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Query

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

    // ── Profile ─────────────────────────────────────────────
    @GET("users/profile.php")
    suspend fun profile(): Response<ProfileResponse>

    @POST("users/profile_update.php")
    suspend fun profileUpdate(@Body body: JsonObject): Response<UpdateProfileResponse>

    @POST("users/settings.php")
    suspend fun profileSettings(@Body body: JsonObject): Response<BasicResponse>

    @POST("users/update.php")
    suspend fun accountUpdate(@Body body: AccountUpdateRequest): Response<MeResponse>

    // ── Image uploads ───────────────────────────────────────
    @Multipart
    @POST("upload/avatar.php")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): Response<UploadResponse>

    @Multipart
    @POST("upload/banner.php")
    suspend fun uploadBanner(@Part file: MultipartBody.Part): Response<UploadResponse>

    @Multipart
    @POST("upload/wallpaper.php")
    suspend fun uploadWallpaper(@Part file: MultipartBody.Part): Response<UploadResponse>

    /* ── Feed & Posts ──────────────────────────────────────────── */

    @GET("feed.php")
    suspend fun feed(
        @Query("scope") scope: String = "all",
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null,
    ): Response<FeedResponse>

    @POST("posts/create.php")
    suspend fun createPost(@Body body: CreatePostRequest): Response<CreatePostResponse>

    @POST("posts/update.php")
    suspend fun updatePost(@Body body: UpdatePostRequest): Response<UpdatePostResponse>

    @POST("posts/delete.php")
    suspend fun deletePost(@Body body: DeletePostRequest): Response<DeletePostResponse>

    @Multipart
    @POST("upload/media.php")
    suspend fun uploadMedia(@Part file: MultipartBody.Part): Response<UploadMediaResponse>

    /* ── Users directory + profile view ─────────────────────── */

    @GET("users/list.php")
    suspend fun usersList(
        @Query("q") q: String? = null,
        @Query("limit") limit: Int = 24,
        @Query("offset") offset: Int = 0,
        @Query("exclude_connected") excludeConnected: Int? = null,
    ): Response<UsersListResponse>

    @GET("users/view.php")
    suspend fun userView(@Query("id") id: Int): Response<ProfileViewResponse>

    /* ── Connection actions ─────────────────────────────────── */

    @POST("connections/request.php")
    suspend fun connectionRequest(@Body body: ConnectionRequest): Response<ConnectionRequestResponse>

    @POST("connections/accept.php")
    suspend fun connectionAccept(@Body body: ConnectionRequest): Response<ConnectionActionResponse>

    @POST("connections/block.php")
    suspend fun connectionBlock(@Body body: ConnectionRequest): Response<ConnectionActionResponse>

    /* ── Messages ─────────────────────────────────────────────── */

    @GET("messages/conversations.php")
    suspend fun conversations(): Response<ConversationsListResponse>

    @GET("messages/thread.php")
    suspend fun messageThread(
        @Query("conversation_id") conversationId: Long,
        @Query("before_id") beforeId: Long? = null,
        @Query("limit") limit: Int = 50,
    ): Response<ThreadResponse>

    @GET("messages/stream.php")
    suspend fun messagesStream(@Query("since") since: Long): Response<StreamResponse>

    @POST("messages/open.php")
    suspend fun openConversation(@Body body: OpenConversationRequest): Response<OpenConversationResponse>

    @POST("messages/send.php")
    suspend fun sendMessage(@Body body: SendMessageRequest): Response<SendMessageResponse>

    @POST("messages/read.php")
    suspend fun markRead(@Body body: MarkReadRequest): Response<BasicMessageResponse>

    /* ── Reactions ─────────────────────────────────────────── */

    @POST("reactions/toggle.php")
    suspend fun toggleReaction(
        @Body body: ToggleReactionRequest
    ): Response<ToggleReactionResponse>

    /* ── Comments ──────────────────────────────────────────── */

    @GET("comments/list.php")
    suspend fun commentsList(
        @Query("post_id") postId: Int,
        @Query("limit")   limit: Int = 50,
        @Query("offset")  offset: Int = 0,
    ): Response<CommentsListResponse>

    @POST("comments/create.php")
    suspend fun createComment(
        @Body body: CreateCommentRequest
    ): Response<CreateCommentResponse>

    @POST("comments/delete.php")
    suspend fun deleteComment(
        @Body body: DeleteCommentRequest
    ): Response<DeleteCommentResponse>

    /* ── Notifications ─────────────────────────────────────────── */

    @GET("notifications/list.php")
    suspend fun notificationsList(
        @Query("unread_only") unreadOnly: Int? = null,
        @Query("limit") limit: Int = 50,
    ): Response<NotificationsListResponse>

    @POST("notifications/read.php")
    suspend fun markNotificationRead(
        @Body body: MarkNotificationReadRequest,
    ): Response<MarkReadResponse>

    @POST("notifications/delete.php")
    suspend fun deleteNotification(
        @Body body: DeleteNotificationRequest,
    ): Response<DeleteNotificationResponse>
}