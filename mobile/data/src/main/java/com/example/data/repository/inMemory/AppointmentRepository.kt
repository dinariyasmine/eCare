package com.example.data.repository.inMemory

import android.annotation.SuppressLint
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import java.text.SimpleDateFormat
import java.util.*

class AppointmentRepository  {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val appointments = mutableListOf<Appointment>()

    init {
        // Add some dummy data
        appointments.addAll(
            listOf(
                Appointment(
                    id = 1,
                    doctor_id = 101,
                    patient_id = 201,
                    start_time = sdf.parse("2025-04-13 10:00:00") as Date,
                    end_time = sdf.parse("2025-04-13 11:00:00") as Date,
                    status = AppointmentStatus.CONFIRMED,
                    QR_code = "QR001",
                    date = sdf.parse("2025-04-13") as Date
                ),
                Appointment(
                    id = 2,
                    doctor_id = 102,
                    patient_id = 202,
                    start_time = sdf.parse("2025-04-14 14:30:00") as Date,
                    end_time = sdf.parse("2025-04-14 15:30:00") as Date,
                    status = AppointmentStatus.IN_PROGRESS,
                    QR_code = "QR002",
                    date = sdf.parse("2025-04-14") as Date
                ),
                Appointment(
                    id = 3,
                    doctor_id = 103,
                    patient_id = 203,
                    start_time = sdf.parse("2025-04-15 09:15:00") as Date,
                    end_time = sdf.parse("2025-04-15 10:15:00") as Date,
                    status = AppointmentStatus.COMPLETED,
                    QR_code = "QR003",
                    date = sdf.parse("2025-04-15") as Date
                )
            )
        )
    }

    fun getAllAppointments(): List<Appointment> {
        return appointments.toList()
    }

    fun getAppointmentById(id: Int): Appointment? {
        return appointments.find { it.id == id }
    }

    fun createAppointment(appointment: Appointment): Boolean {
        return appointments.add(appointment)
    }

    fun updateAppointment(appointment: Appointment): Boolean {
        val index = appointments.indexOfFirst { it.id == appointment.id }
        if (index != -1) {
            appointments[index] = appointment
            return true
        }
        return false
    }



    fun getAppointmentsByDoctorId(doctorId: Int): List<Appointment> {
        return appointments.filter { it.doctor_id == doctorId }
    }

    fun getAppointmentsByPatientId(patientId: Int): List<Appointment> {
        return appointments.filter { it.patient_id == patientId }
    }

    fun getAppointmentsByStatus(status: AppointmentStatus): List<Appointment> {
        return appointments.filter { it.status == status }
    }
}
