package lol.mycitadel.app.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import lol.mycitadel.app.MyCitadelApp
import lol.mycitadel.app.data.repository.NotificationsRepository

class NotificationSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "NotificationSyncWorker executing background check...")
        return try {
            val app = applicationContext as MyCitadelApp
            val notificationsRepo = app.notificationsRepository

            PushNotificationManager.createNotificationChannel(applicationContext)
            PushNotificationManager.registerFcmToken(notificationsRepo)

            when (val result = notificationsRepo.list(unreadOnly = true)) {
                is NotificationsRepository.ListResult.Success -> {
                    result.notifications.forEach { notif ->
                        if (!notif.read && PushNotificationManager.shouldShowNotification(applicationContext, notif.id)) {
                            PushNotificationManager.showLocalNotification(applicationContext, notif)
                        }
                    }
                    Result.success()
                }
                is NotificationsRepository.ListResult.Failure -> {
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Background NotificationSyncWorker failed", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "NotificationSyncWorker"
    }
}
