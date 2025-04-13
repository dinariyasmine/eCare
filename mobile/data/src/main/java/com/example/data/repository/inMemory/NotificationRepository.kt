package com.example.data.repository.inMemory

import android.annotation.SuppressLint
import com.example.data.model.Notification
import java.text.SimpleDateFormat
import java.util.*

class InMemoryNotificationRepository {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    @SuppressLint("SimpleDateFormat")
    private val timeSdf = SimpleDateFormat("HH:mm:ss")
    private val notifications = mutableListOf<Notification>()

    init {
        // Initialize with dummy data
        notifications.addAll(
            listOf(
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
        )
    }

    fun getAllNotifications(): List<Notification> {
        return notifications.toList()
    }

    fun getNotificationById(id: Int): Notification? {
        return notifications.find { it.id == id }
    }

    fun getNotificationsByUserId(userId: Int): List<Notification> {
        return notifications.filter { it.user_id == userId }
    }

    fun createNotification(notification: Notification): Boolean {
        // Check if notification with the same ID already exists
        if (notifications.any { it.id == notification.id }) {
            return false
        }
        return notifications.add(notification)
    }

    fun updateNotification(notification: Notification): Boolean {
        val index = notifications.indexOfFirst { it.id == notification.id }
        if (index != -1) {
            notifications[index] = notification
            return true
        }
        return false
    }

    fun deleteNotification(id: Int): Boolean {
        val initialSize = notifications.size
        notifications.removeIf { it.id == id }
        return notifications.size < initialSize
    }

    fun getNotificationsByType(type: String): List<Notification> {
        return notifications.filter { it.type == type }
    }

    fun getRecentNotifications(limit: Int = 5): List<Notification> {
        return notifications.sortedByDescending {
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
