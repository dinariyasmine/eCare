package com.example.data.repository

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.data.model.Appointment
import com.example.data.model.AppointmentRequest
import com.example.data.model.AppointmentStatus
import com.example.data.retrofit.AppointmentEndpoint
import com.example.data.room.AppointmentDao
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AppointmentRepository (private val appointmentEndpoint: AppointmentEndpoint, private val appointmentDao: AppointmentDao ) {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    suspend fun getAppointmentsByPatient(id: Int): List<Appointment> {
        val appointments = appointmentEndpoint.getAppointsByPatient(id)
        // idk how nzid condition bach soit using endpoint soit using dao
        return appointments
    }

    suspend fun getAppointmentsByDoctor(id: Int): List<Appointment> {
        val appointments = appointmentEndpoint.getAppointsByDoctor(id)
        // idk how nzid condition bach soit using endpoint soit using dao
        return appointments
    }

    suspend fun createAppointment(appointment: AppointmentRequest) {
        appointmentEndpoint.addAppointment(appointment)
    }

    suspend fun updateAppointment(id: Int, appointment: Appointment) {
        appointmentEndpoint.updateAppointment(id, appointment)
    }

    suspend fun deleteAppointment(id: Int) {
        appointmentEndpoint.deleteAppointment(id)
    }
}
