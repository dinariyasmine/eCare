// mobile/data/src/main/java/com/example/data/repository/NotificationRepository.kt
package com.example.data.repository

import android.util.Log
import com.example.data.model.Notification
import com.example.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationRepository(private val apiService: ApiService) {

    // Get all notifications from server
    fun getNotifications(): Flow<List<Notification>> = flow {
        try {
            val response = apiService.getNotifications()
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Get unread notifications from server
    fun getUnreadNotifications(): Flow<List<Notification>> = flow {
        try {
            val response = apiService.getUnreadNotifications()
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Mark notification as read on server
    suspend fun markAsRead(notificationId: Int): Boolean {
        return try {
            Log.d("NotificationRepo", "Marking notification $notificationId as read")
            val response = apiService.markNotificationAsRead(notificationId)
            Log.d(
                "NotificationRepo",
                "Mark as read response: ${response.code()}, ${response.message()}"
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error marking notification as read: ${e.message}")
            false
        }
    }

    // Mark all notifications as read on server
    suspend fun markAllAsRead(): Boolean {
        return try {
            Log.d("NotificationRepo", "Marking all notifications as read")
            val response = apiService.markAllNotificationsAsRead()
            Log.d(
                "NotificationRepo",
                "Mark all as read response: ${response.code()}, ${response.message()}"
            )
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error marking all notifications as read: ${e.message}")
            false
        }
    }
}
