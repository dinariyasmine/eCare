// mobile/data/src/main/java/com/example/data/model/Notification.kt
package com.example.data.model

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val description: String,
    val type: NotificationType,
    val timestamp: Date,
    val isRead: Boolean = false,
    val relatedId: String? = null
)

enum class NotificationType {
    APPOINTMENT_CONFIRMED,
    APPOINTMENT_RESCHEDULED,
    APPOINTMENT_CANCELED,
    APPOINTMENT_REMINDER,
    PRESCRIPTION_CREATED,
    PRESCRIPTION_UPDATED,
    MEDICATION_REMINDER
}
