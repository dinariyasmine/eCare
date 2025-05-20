package com.example.data.model

import java.util.Date

data class Notification(
    val id: Int,  // Changed from String to Int
    val user: User,  // Added user object
    val title: String,
    val description: String,
    val date_creation: String,  // Added date_creation
    val time_creation: String,  // Added time_creation
    val type: String,  // Changed from enum to String
    val read: Boolean,  // Changed from isRead to read
    val appointment_id: Int?,  // Added appointment_id
    val prescription_id: Int?,  // Added prescription_id
    val created_at: String,  // Added created_at
    val updated_at: String  // Added updated_at
) {
    // Helper property to convert type string to enum if needed
    val notificationType: NotificationType
        get() = when(type) {
            "appointment_scheduled" -> NotificationType.APPOINTMENT_CONFIRMED
            "appointment_reminder" -> NotificationType.APPOINTMENT_REMINDER
            "appointment_canceled" -> NotificationType.APPOINTMENT_CANCELED
            "prescription_created" -> NotificationType.PRESCRIPTION_CREATED
            "prescription_updated" -> NotificationType.PRESCRIPTION_UPDATED
            "medication_reminder" -> NotificationType.MEDICATION_REMINDER
            else -> NotificationType.APPOINTMENT_CONFIRMED
        }

    // Helper property to get a combined timestamp
    val timestamp: Date
        get() {
            val dateTimeString = "${date_creation}T${time_creation}"
            return try {
                val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", java.util.Locale.getDefault())
                formatter.parse(dateTimeString) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        }

    // Helper property to maintain compatibility with existing code
    val isRead: Boolean
        get() = read

    // Helper property to get related ID (either appointment or prescription)
    val relatedId: String?
        get() = when {
            appointment_id != null -> appointment_id.toString()
            prescription_id != null -> prescription_id.toString()
            else -> null
        }
}

enum class NotificationType {
    APPOINTMENT_CONFIRMED,
    APPOINTMENT_RESCHEDULED,
    APPOINTMENT_CANCELED,
    APPOINTMENT_REMINDER,
    PRESCRIPTION_CREATED,
    PRESCRIPTION_UPDATED,
    MEDICATION_REMINDER
}
