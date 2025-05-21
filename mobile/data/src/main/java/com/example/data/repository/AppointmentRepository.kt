package com.example.data.repository

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.data.model.Appointment
import com.example.data.model.AppointmentDto
import com.example.data.model.AppointmentRequest
import com.example.data.model.AppointmentStatus
import com.example.data.retrofit.AppointmentEndpoint
import com.example.data.room.AppointmentDao
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class AppointmentRepository(
    private val appointmentEndpoint: AppointmentEndpoint,
    private val appointmentDao: AppointmentDao
) {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    // Helper function to convert DTO to Appointment
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertDtoToAppointment(dto: AppointmentDto): Appointment {
        val formatter = DateTimeFormatter.ISO_DATE_TIME

        return Appointment(
            id = dto.id,
            doctor_id = dto.doctor,
            patient_id = dto.patient,
            start_time = LocalDateTime.parse(dto.start_time, formatter),
            end_time = LocalDateTime.parse(dto.end_time, formatter),
            name = dto.name,
            gender = dto.gender,
            age = dto.age,
            problem_description = dto.problem_description,
            status = AppointmentStatus.valueOf(dto.status.uppercase()),
            QR_code = dto.qr_Code
        )
    }

    // Fetches appointments by patient (converts DTO to Appointment)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAppointmentsByPatient(id: Int): List<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                val response = appointmentEndpoint.getAppointsByPatient(id)
                response.map { convertDtoToAppointment(it) }
            } catch (e: Exception) {
                // Fallback to local DB if API fails
                appointmentDao.getAppointmentsByPatient(id)
            }
        }
    }

    // Fetches appointments by doctor (converts DTO to Appointment)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAppointmentsByDoctor(id: Int): List<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                val response = appointmentEndpoint.getAppointsByDoctor(id)
                response.map { convertDtoToAppointment(it) }
            } catch (e: Exception) {
                // Fallback to local DB if API fails
                appointmentDao.getAppointmentsByDoctor(id)
            }
        }
    }

    // Creates an appointment (handles API + local DB)
    suspend fun createAppointment(appointment: AppointmentRequest) {
        withContext(Dispatchers.IO) {
            try {
                val request = appointment.copy(
                    start_time = appointment.start_time.format(DateTimeFormatter.ISO_DATE_TIME),
                    end_time = appointment.end_time.format(DateTimeFormatter.ISO_DATE_TIME)
                )
                println("Sending appointment request: $request")
                appointmentEndpoint.addAppointment(request)
                // Optionally save to local DB
                // appointmentDao.insert(appointment)
            } catch (e: Exception) {
                println("Error: $e")
                throw e // Or handle offline case
            }
        }
    }

    // Updates an appointment (handles API + local DB)
    suspend fun updateAppointment(id: Int, appointment: Appointment) {
        withContext(Dispatchers.IO) {
            try {
                appointmentEndpoint.updateAppointment(id, appointment)
                // Optionally update local DB
                // appointmentDao.update(appointment)
            } catch (e: Exception) {
                throw e // Or handle offline case
            }
        }
    }

    // Deletes an appointment (handles API + local DB)
    suspend fun deleteAppointment(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                appointmentEndpoint.deleteAppointment(id)
                // Optionally delete from local DB
                // appointmentDao.delete(id)
            } catch (e: Exception) {
                throw e // Or handle offline case
            }
        }
    }
}