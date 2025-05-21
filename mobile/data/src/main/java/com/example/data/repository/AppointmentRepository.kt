package com.example.data.repository

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
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
        Log.d("AppointmentRepository", "Converting DTO to Appointment: ${dto.id}")
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
                Log.d("AppointmentRepository", "Fetching appointments for patient ID: $id from API")
                val response = appointmentEndpoint.getAppointsByPatient(id)
                Log.d("AppointmentRepository", "Received ${response.size} appointments from API for patient")
                response.forEach { dto ->
                    Log.d("AppointmentRepository", """
                        Raw DTO from API:
                        ID: ${dto.id}
                        Doctor: ${dto.doctor}
                        Patient: ${dto.patient}
                        Start Time: ${dto.start_time}
                        Status: ${dto.status}
                    """.trimIndent())
                }
                val appointments = response.map { convertDtoToAppointment(it) }
                Log.d("AppointmentRepository", "Converted ${appointments.size} appointments for patient")
                appointments
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error fetching from API, falling back to local DB", e)
                // Fallback to local DB if API fails
                val localAppointments = appointmentDao.getAppointmentsByPatient(id)
                Log.d("AppointmentRepository", "Retrieved ${localAppointments.size} appointments from local DB")
                localAppointments
            }
        }
    }

    // Fetches appointments by doctor (converts DTO to Appointment)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAppointmentsByDoctor(id: Int): List<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AppointmentRepository", "Fetching appointments for doctor ID: $id from API")
                val response = appointmentEndpoint.getAppointsByDoctor(id)
                Log.d("AppointmentRepository", "Received ${response.size} appointments from API for doctor")
                response.forEach { dto ->
                    Log.d("AppointmentRepository", """
                        Raw DTO from API:
                        ID: ${dto.id}
                        Doctor: ${dto.doctor}
                        Patient: ${dto.patient}
                        Start Time: ${dto.start_time}
                        Status: ${dto.status}
                    """.trimIndent())
                }
                val appointments = response.map { convertDtoToAppointment(it) }
                Log.d("AppointmentRepository", "Converted ${appointments.size} appointments for doctor")
                appointments
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error fetching from API, falling back to local DB", e)
                // Fallback to local DB if API fails
                val localAppointments = appointmentDao.getAppointmentsByDoctor(id)
                Log.d("AppointmentRepository", "Retrieved ${localAppointments.size} appointments from local DB")
                localAppointments
            }
        }
    }

    // Creates an appointment (handles API + local DB)
    suspend fun createAppointment(appointment: AppointmentRequest) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("AppointmentRepository", "Creating new appointment request: $appointment")
                val request = appointment.copy(
                    start_time = appointment.start_time.format(DateTimeFormatter.ISO_DATE_TIME),
                    end_time = appointment.end_time.format(DateTimeFormatter.ISO_DATE_TIME)
                )
                Log.d("AppointmentRepository", "Sending formatted request to API: $request")
                appointmentEndpoint.addAppointment(request)
                Log.d("AppointmentRepository", "Appointment created successfully in API")
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error creating appointment", e)
                throw e
            }
        }
    }

    // Updates an appointment (handles API + local DB)
    suspend fun updateAppointment(id: Int, appointment: Appointment) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("AppointmentRepository", "Updating appointment ID: $id")
                appointmentEndpoint.updateAppointment(id, appointment)
                Log.d("AppointmentRepository", "Appointment updated successfully in API")
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error updating appointment", e)
                throw e
            }
        }
    }

    // Deletes an appointment (handles API + local DB)
    suspend fun deleteAppointment(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("AppointmentRepository", "Deleting appointment ID: $id")
                appointmentEndpoint.deleteAppointment(id)
                Log.d("AppointmentRepository", "Appointment deleted successfully from API")
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error deleting appointment", e)
                throw e
            }
        }
    }
}