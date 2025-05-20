package com.example.core.navigation

import android.content.Context
import android.content.Intent

interface NavigationHandler {
    fun getMainActivityIntent(context: Context, notificationType: String?, notificationId: String?): Intent
}
