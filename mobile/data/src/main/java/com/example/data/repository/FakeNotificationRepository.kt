package com.example.data.repository

import com.example.data.model.Notification
import com.example.data.model.NotificationType
import kotlinx.coroutines.delay

class FakeNotificationRepository : NotificationRepository {
    private val fakeNotifications = mutableListOf(
        Notification(
            id = "1",
            title = "Appointment Confirmed",
            message = "Your appointment has been confirmed for March 23, 2025, at 10:00 AM",
            timeAgo = "9 min ago",
            groupHeader = "Today",
            isRead = false,
            type = NotificationType.APPOINTMENT_CONFIRMED,
            createdAt = System.currentTimeMillis()
        ),
        Notification(
            id = "2",
            title = "Appointment Rescheduled",
            message = "Your appointment has been rescheduled to March 25, at 4:00 PM",
            timeAgo = "14 min ago",
            groupHeader = "Today",
            isRead = false,
            type = NotificationType.APPOINTMENT_RESCHEDULED,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 14
        )
    )

    override suspend fun getNotifications(): List<Notification> {
        delay(500) // Simulate network delay
        return fakeNotifications
    }

    override suspend fun markAsRead(notificationId: String) {
        fakeNotifications.replaceAll {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }
}