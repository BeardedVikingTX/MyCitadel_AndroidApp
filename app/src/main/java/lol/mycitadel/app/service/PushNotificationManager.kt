package lol.mycitadel.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import lol.mycitadel.app.MainActivity
import lol.mycitadel.app.R
import lol.mycitadel.app.data.network.NotificationDto
import lol.mycitadel.app.data.repository.NotificationsRepository
import java.util.concurrent.TimeUnit

object PushNotificationManager {

    private const val TAG = "PushNotificationManager"
    private const val CHANNEL_ID = "mycitadel_push_channel"
    private const val PREFS_NAME = "mycitadel_notifications"
    private const val KEY_SEEN_IDS = "seen_notification_ids"
    private const val WORK_NAME = "mycitadel_notification_work"

    private var syncJob: Job? = null
    private val seenNotificationIds = mutableSetOf<Int>()

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Citadel Push Notifications",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Instant notifications for updates, comments, and messages"
                    enableLights(true)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun scheduleBackgroundWork(context: Context) {
        try {
            val workRequest = PeriodicWorkRequestBuilder<NotificationSyncWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest,
            )
            Log.d(TAG, "Background WorkManager periodic sync scheduled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule background notification WorkManager", e)
        }
    }

    fun registerFcmToken(notificationsRepo: NotificationsRepository) {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d(TAG, "FCM Token retrieved: $token")
                CoroutineScope(Dispatchers.IO).launch {
                    notificationsRepo.registerDeviceToken(token)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "FCM token registration error", e)
        }
    }

    fun startNotificationSync(
        context: Context,
        notificationsRepo: NotificationsRepository,
    ) {
        syncJob?.cancel()
        loadSeenIds(context)

        syncJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    when (val result = notificationsRepo.list(unreadOnly = true)) {
                        is NotificationsRepository.ListResult.Success -> {
                            result.notifications.forEach { notif ->
                                if (!notif.read && shouldShowNotification(context, notif.id)) {
                                    markNotificationSeen(context, notif.id)
                                    showLocalNotification(context, notif)
                                }
                            }
                        }
                        is NotificationsRepository.ListResult.Failure -> {
                            Log.w(TAG, "Notification sync failed: ${result.message}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Notification sync loop error", e)
                }
                delay(12_000)
            }
        }
    }

    fun stopNotificationSync() {
        syncJob?.cancel()
        syncJob = null
    }

    fun shouldShowNotification(context: Context, notifId: Int): Boolean {
        loadSeenIds(context)
        return !seenNotificationIds.contains(notifId)
    }

    fun markNotificationSeen(context: Context, notifId: Int) {
        seenNotificationIds.add(notifId)
        saveSeenIds(context)
    }

    fun showLocalNotification(context: Context, notif: NotificationDto) {
        val title = notif.title.ifBlank {
            when {
                notif.type.contains("comment") -> "New Comment"
                notif.type.contains("message") -> "New Message"
                notif.type.contains("reaction") -> "New Reaction"
                notif.type.contains("connection") -> "Connection Request"
                else -> "MyCitadel Notification"
            }
        }

        val actorName = notif.actor?.displayName ?: notif.actor?.username
        val bodyText = notif.body ?: when {
            actorName != null -> "$actorName interacted with your profile"
            else -> "You have a new update in MyCitadel."
        }

        val payloadObj = try {
            notif.payload?.let { Json.decodeFromJsonElement(JsonObject.serializer(), it) }
        } catch (_: Exception) { null }

        val postId = payloadObj?.get("post_id")?.jsonPrimitive?.intOrNull
            ?: payloadObj?.get("postId")?.jsonPrimitive?.intOrNull
            ?: extractIntParam(notif.link, "post_id")

        val conversationId = payloadObj?.get("conversation_id")?.jsonPrimitive?.longOrNull
            ?: payloadObj?.get("conversationId")?.jsonPrimitive?.longOrNull
            ?: extractLongParam(notif.link, "conversation_id")

        val dataMap = mutableMapOf<String, String>(
            "notification_id" to notif.id.toString(),
            "type" to notif.type,
        )
        postId?.let { dataMap["post_id"] = it.toString() }
        conversationId?.let { dataMap["conversation_id"] = it.toString() }
        notif.actor?.id?.let { dataMap["actor_id"] = it.toString() }

        showNotificationWithDetails(
            context = context,
            id = notif.id,
            title = title,
            body = bodyText,
            data = dataMap,
        )
    }

    fun showNotificationWithDetails(
        context: Context,
        id: Int,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap(),
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (k, v) -> putExtra(k, v) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(id, builder.build())
    }

    private fun extractIntParam(link: String?, key: String): Int? {
        if (link.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(link)
            uri.getQueryParameter(key)?.toIntOrNull()
                ?: uri.lastPathSegment?.toIntOrNull()
        } catch (_: Exception) { null }
    }

    private fun extractLongParam(link: String?, key: String): Long? {
        if (link.isNullOrBlank()) return null
        return try {
            val uri = Uri.parse(link)
            uri.getQueryParameter(key)?.toLongOrNull()
                ?: uri.lastPathSegment?.toLongOrNull()
        } catch (_: Exception) { null }
    }

    private fun loadSeenIds(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getStringSet(KEY_SEEN_IDS, emptySet()) ?: emptySet()
        seenNotificationIds.clear()
        seenNotificationIds.addAll(raw.mapNotNull { it.toIntOrNull() })
    }

    private fun saveSeenIds(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = seenNotificationIds.toList().takeLast(200).map { it.toString() }.toSet()
        prefs.edit().putStringSet(KEY_SEEN_IDS, set).apply()
    }
}
