package com.example.data.repository

import android.annotation.SuppressLint
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppointmentRepository {
    // Use separate formatters for date and datetime
    @SuppressLint("SimpleDateFormat")
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private suspend fun parseDateOrNull(dateString: String, format: SimpleDateFormat): Date? {
        return try {
            format.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Appointment> = withContext(Dispatchers.IO) {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)

        return@withContext listOf(
            Appointment(
                id = 1,
                doctor_id = 101,
                patient_id = 201,
                start_time = parseDateOrNull("2025-04-13 10:00:00", dateTimeFormat) ?: Date(),
                end_time = parseDateOrNull("2025-04-13 11:00:00", dateTimeFormat) ?: Date(),
                status = AppointmentStatus.CONFIRMED,
                QR_code = "QR001",
                date = parseDateOrNull("2025-04-13", dateFormat) ?: Date()
            ),
            Appointment(
                id = 2,
                doctor_id = 102,
                patient_id = 202,
                start_time = parseDateOrNull("2025-04-14 14:30:00", dateTimeFormat) ?: Date(),
                end_time = parseDateOrNull("2025-04-14 15:30:00", dateTimeFormat) ?: Date(),
                status = AppointmentStatus.IN_PROGRESS,
                QR_code = "QR002",
                date = parseDateOrNull("2025-04-14", dateFormat) ?: Date()
            ),
            Appointment(
                id = 3,
                doctor_id = 103,
                patient_id = 203,
                start_time = parseDateOrNull("2025-04-15 09:15:00", dateTimeFormat) ?: Date(),
                end_time = parseDateOrNull("2025-04-15 10:15:00", dateTimeFormat) ?: Date(),
                status = AppointmentStatus.COMPLETED,
                QR_code = "QR003",
                date = parseDateOrNull("2025-04-15", dateFormat) ?: Date()
            )
        )
    }

    suspend fun getAllAppointments(): List<Appointment> {
        return simulateDatabaseCall()
    }

    suspend fun getAppointmentById(id: Int): Appointment? {
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun createAppointment(appointment: Appointment): Boolean {
        // Simulate creating an appointment in the database
        kotlinx.coroutines.delay(500)
        return true
    }

    suspend fun updateAppointment(appointment: Appointment): Boolean {
        // Simulate updating an appointment in the database
        kotlinx.coroutines.delay(500)
        return true
    }

    suspend fun deleteAppointment(id: Int): Boolean {
        // Simulate deleting an appointment from the database
        kotlinx.coroutines.delay(500)
        return true
    }

    suspend fun getAppointmentsByDoctorId(doctorId: Int): List<Appointment> {
        return simulateDatabaseCall().filter { it.doctor_id == doctorId }
    }

    suspend fun getAppointmentsByPatientId(patientId: Int): List<Appointment> {
        return listOf(
            Appointment(
                id = 1,
                doctor_id = 101,
                patient_id = 201,
                start_time = parseDateOrNull("2025-04-16 01:00:00", dateTimeFormat) ?: Date(),
                end_time = parseDateOrNull("2025-04-16 11:00:00", dateTimeFormat) ?: Date(),
                status = AppointmentStatus.CONFIRMED,
                QR_code = "QR001",
                date = parseDateOrNull("2025-04-16", dateFormat) ?: Date()
            ),
            Appointment(
                id = 2,
                doctor_id = 102,
                patient_id = 202,
                start_time = parseDateOrNull("2025-04-14 14:30:00", dateTimeFormat) ?: Date(),
                end_time = parseDateOrNull("2025-04-14 15:30:00", dateTimeFormat) ?: Date(),
                status = AppointmentStatus.IN_PROGRESS,
                QR_code = "QR002",
                date = parseDateOrNull("2025-04-14", dateFormat) ?: Date()
            ))
    }

    suspend fun getAppointmentsByStatus(status: AppointmentStatus): List<Appointment> {
        return simulateDatabaseCall().filter { it.status == status }
    }
}