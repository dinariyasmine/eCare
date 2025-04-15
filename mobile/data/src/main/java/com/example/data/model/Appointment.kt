package com.example.data.model

import java.util.Date

data class Appointment(
    val id: Int,
    val doctor_id: Int,
    val patient_id: Int,
    val start_time: Date,
    val end_time: Date,
    val status: AppointmentStatus,
    val QR_code: String,
    val date : Date
)

enum class AppointmentStatus {
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED
}