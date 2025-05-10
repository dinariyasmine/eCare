package com.example.data.model

import androidx.room.Entity
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Appointment(
    val id: Int,
    val doctor_id: Int,
    val patient_id: Int,
    val date: LocalDate,
    val start_time: LocalTime,
    val end_time: LocalTime,
    val name: String,
    val gender: String,
    val age: String,
    val problem_description: String,
    val status: AppointmentStatus,
    val QR_code: String
)

enum class AppointmentStatus {
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED
}

data class AppointmentRequest(
    val doctor_id: Int,
    val patient_id: Int,
    val date: LocalDate,
    val start_time: LocalTime,
    val end_time: LocalTime,
    val name: String,
    val gender: String,
    val age: String,
    val problem_description: String
)

