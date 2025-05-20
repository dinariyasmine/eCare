// mobile/data/src/main/java/com/example/data/repository/NotificationRepository.kt
package com.example.data.repository

import com.example.data.model.Notification
import com.example.data.model.NotificationType
import com.example.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.util.Date

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
    suspend fun markAsRead(notificationId: String): Boolean {
        try {
            val response = apiService.markNotificationAsRead(notificationId)
            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }

    // Mark all notifications as read on server
    suspend fun markAllAsRead(): Boolean {
        try {
            val response = apiService.markAllNotificationsAsRead()
            return response.isSuccessful
        } catch (e: Exception) {
            return false
        }
    }
}
