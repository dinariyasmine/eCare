package com.example.data.repository

import android.annotation.SuppressLint
import com.example.data.model.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NotificationRepository {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    @SuppressLint("SimpleDateFormat")
    private val timeSdf = SimpleDateFormat("HH:mm:ss")

    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Notification> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        return@withContext listOf(
            Notification(
                id = 1,
                title = "Welcome Notification",
                description = "Welcome to the eCare Mobile app!",
                user_id = 101,
                date_creation = sdf.parse("2025-04-10") as Date,
                time_creation = timeSdf.parse("10:00:00") as Date,
                type = "INFO"
            ),
            Notification(
                id = 2,
                title = "Appointment Reminder",
                description = "You have an appointment scheduled tomorrow at 10:00 AM.",
                user_id = 102,
                date_creation = sdf.parse("2025-04-11") as Date,
                time_creation = timeSdf.parse("09:00:00") as Date,
                type = "REMINDER"
            ),
            Notification(
                id = 3,
                title = "System Update",
                description = "The app will undergo maintenance tonight from 12:00 AM to 2:00 AM.",
                user_id = 103,
                date_creation = sdf.parse("2025-04-12") as Date,
                time_creation = timeSdf.parse("18:00:00") as Date,
                type = "ALERT"
            )
        )
    }

    suspend fun getAllNotifications(): List<Notification> {
        return simulateDatabaseCall()
    }

    suspend fun getNotificationById(id: Int): Notification? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun getNotificationsByUserId(userId: Int): List<Notification> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.user_id == userId }
    }

    suspend fun createNotification(notification: Notification): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        return !simulateDatabaseCall().any { it.id == notification.id }
    }

    suspend fun updateNotification(notification: Notification): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == notification.id }
    }

    suspend fun deleteNotification(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }

    suspend fun getNotificationsByType(type: String): List<Notification> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.type == type }
    }

    suspend fun getRecentNotifications(limit: Int = 5): List<Notification> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().sortedByDescending {
            val dateTime = Calendar.getInstance()
            dateTime.time = it.date_creation
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = it.time_creation
            dateTime.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            dateTime.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            dateTime.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND))
            dateTime.time
        }.take(limit)
    }
}
