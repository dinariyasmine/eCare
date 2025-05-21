// mobile/app/src/main/java/com/example/ecare_mobile/navigation/AppNavigationHandler.kt
package com.example.ecare_mobile.navigation

import android.content.Context
import android.content.Intent
import com.example.core.navigation.NavigationHandler
import com.example.ecare_mobile.MainActivity

class AppNavigationHandler : NavigationHandler {
    override fun getMainActivityIntent(context: Context, notificationType: String?, notificationId: String?): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("notification_type", notificationType)
            putExtra("notification_id", notificationId)
        }
    }
}
