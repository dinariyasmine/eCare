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

    fun deleteAppointment(id: Int): Boolean {
        return appointments.removeIf { it.id == id }
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
