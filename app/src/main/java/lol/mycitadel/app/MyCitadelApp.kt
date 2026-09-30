package lol.mycitadel.app

import android.app.Application
import lol.mycitadel.app.data.network.CitadelClient
import lol.mycitadel.app.data.network.PersistentCookieJar
import lol.mycitadel.app.data.repository.AuthRepository
import lol.mycitadel.app.data.repository.DashboardRepository
import lol.mycitadel.app.data.repository.ProfileRepository
import lol.mycitadel.app.data.repository.FeedRepository
import lol.mycitadel.app.data.repository.UsersRepository
import lol.mycitadel.app.data.repository.MessagesRepository
import lol.mycitadel.app.data.repository.NotificationsRepository

class MyCitadelApp : Application() {

    val cookieJar: PersistentCookieJar by lazy { PersistentCookieJar(this) }
    val apiClient: CitadelClient       by lazy { CitadelClient(cookieJar) }
    val authRepository: AuthRepository by lazy {
        AuthRepository(apiClient.api, apiClient)
    }
    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository(apiClient.api)
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(apiClient.api, applicationContext)
    }

    val feedRepository: FeedRepository by lazy {
        FeedRepository(apiClient.api, applicationContext)
    }

    val usersRepository: UsersRepository by lazy {
        UsersRepository(apiClient.api)
    }

    val messagesRepository: MessagesRepository by lazy {
        MessagesRepository(apiClient.api, applicationContext)
    }

    val notificationsRepository: NotificationsRepository by lazy {
        NotificationsRepository(apiClient.api)
    }

}