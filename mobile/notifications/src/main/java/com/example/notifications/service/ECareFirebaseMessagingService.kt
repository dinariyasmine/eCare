package com.example.notifications.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.core.navigation.NavigationHandler
import com.example.data.network.ApiClient
import com.example.data.network.DeviceRegistration
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ECareFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private var navigationHandler: NavigationHandler? = null

        fun setNavigationHandler(handler: NavigationHandler) {
            navigationHandler = handler
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains notification payload
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "eCare Notification"
            val body = notification.body ?: "You have a new notification"

            // Get data payload
            val notificationType = remoteMessage.data["notification_type"] ?: "default"
            val notificationId = remoteMessage.data["notification_id"] ?: "0"

            // Show notification
            showNotification(title, body, notificationType, notificationId)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to your backend
        sendRegistrationToServer(token)
    }

    private fun showNotification(title: String, body: String, type: String, id: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ecare_channel"

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "eCare Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "eCare app notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent for notification tap action
        val intent = if (navigationHandler != null) {
            navigationHandler?.getMainActivityIntent(this, type, id)
        } else {
            // Fallback to a generic intent if navigation handler is not set
            Intent().apply {
                action = "com.example.ecare_mobile.OPEN_NOTIFICATION"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("notification_type", type)
                putExtra("notification_id", id)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Show notification
        notificationManager.notify(id.hashCode(), notificationBuilder.build())
    }

    private fun sendRegistrationToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val deviceInfo = DeviceRegistration(registration_id = token)
                ApiClient.apiService.registerDevice(deviceInfo)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
