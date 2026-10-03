package lol.mycitadel.app.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import lol.mycitadel.app.MyCitadelApp

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM token received: $token")
        try {
            val app = applicationContext as MyCitadelApp
            PushNotificationManager.registerFcmToken(app.notificationsRepository)
        } catch (e: Exception) {
            Log.e(TAG, "Error registering refreshed FCM token with backend", e)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received in background/foreground. From: ${remoteMessage.from}")

        PushNotificationManager.createNotificationChannel(applicationContext)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: remoteMessage.data["subject"]
            ?: remoteMessage.data["heading"]
            ?: "MyCitadel"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: remoteMessage.data["content"]
            ?: "New activity in MyCitadel."

        val notifId = remoteMessage.data["id"]?.toIntOrNull()
            ?: (System.currentTimeMillis() % 10000).toInt()

        PushNotificationManager.showNotificationWithDetails(
            context = applicationContext,
            id = notifId,
            title = title,
            body = body,
            data = remoteMessage.data,
        )
    }

    companion object {
        private const val TAG = "CitadelFCM"
    }
}
