package com.example.appointment.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.appointment.data.model.Appointment
import com.example.appointment.data.model.Doctor
import com.example.appointment.data.model.Status
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class AppointmentRepository {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val tomorrow = today.plusDays(1)

    private val doctors = listOf(
        Doctor(1, "John Smith", "Cardiologist", "https://example.com/doctor1.jpg"),
        Doctor(2, "Sarah Johnson", "Dermatologist", "https://example.com/doctor2.jpg"),
        Doctor(3, "Michael Brown", "Pediatrician", "https://example.com/doctor3.jpg")
    )

    private val appointments = listOf(
        Appointment(1,
            doctors[1],
            "john patient",
            date = tomorrow,
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0),
            status = Status.Confirmed,
            qrCode = "lolol"),
        Appointment(2,
            doctors[2],
            "john patient",
            date = today,
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0),
            status = Status.Completed,
            qrCode = "lolol"),
        Appointment(3,
            doctors[0],
            "john patient",
            date = today,
            startTime = LocalTime.of(12, 30),
            endTime = LocalTime.of(13, 30),
            status = Status.InProgress,
            qrCode = "lolol"),
        Appointment(4,
            doctors[2],
            "john patient",
            date = yesterday,
            startTime = LocalTime.of(7, 0),
            endTime = LocalTime.of(8, 0),
            status = Status.Completed,
            qrCode = "lolol"),
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAppointments(): List<Appointment> {
        return appointments
    }

    fun getAppointmentsForDate(date: LocalDate): List<Appointment> {
        return appointments.filter { it.date == date }
            .sortedBy { it.startTime }
    }


}