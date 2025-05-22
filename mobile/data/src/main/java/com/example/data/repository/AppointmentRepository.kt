package com.example.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.data.model.Appointment
import com.example.data.model.AppointmentDto
import com.example.data.model.AppointmentRequest
import com.example.data.model.AppointmentStatus
import com.example.data.retrofit.AppointmentEndpoint
import com.example.data.room.AppointmentDao
import com.example.data.work.AppointmentSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

@RequiresApi(Build.VERSION_CODES.O)
class AppointmentRepository(
    private val appointmentEndpoint: AppointmentEndpoint,
    private val appointmentDao: AppointmentDao,
    private val syncRepository: AppointmentSyncRepository
) {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    // Helper function to convert DTO to Appointment
    private fun convertDtoToAppointment(dto: AppointmentDto): Appointment {
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
            QR_code = dto.qr_Code,
            doctor_name = dto.doctor_name ?: "",
            doctor_specialty = dto.doctor_specialty ?: ""
        )
    }

    // Fetches appointments by patient with offline support
    suspend fun getAppointmentsByPatient(id: Int): List<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                // Try to fetch from API first
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

                // Cache the appointments locally
                appointments.forEach { appointmentDao.insertAppointment(it) }

                appointments
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error fetching from API, using local DB", e)
                // Fallback to local DB if API fails
                appointmentDao.getAppointmentsByPatient(id)
            }
        }
    }

    // Fetches appointments by doctor with offline support
    suspend fun getAppointmentsByDoctor(id: Int): List<Appointment> {
        return withContext(Dispatchers.IO) {
            try {
                // Try to fetch from API first
                val response = appointmentEndpoint.getAppointsByDoctor(id)
                val appointments = response.map { convertDtoToAppointment(it) }

                // Cache the appointments locally
                appointments.forEach { appointmentDao.insertAppointment(it) }

                appointments
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error fetching from API, using local DB", e)
                // Fallback to local DB if API fails
                appointmentDao.getAppointmentsByDoctor(id)
            }
        }
    }

    // Gets single appointment with offline support
    suspend fun getAppointmentById(id: Int): Appointment? {
        return withContext(Dispatchers.IO) {
            try {
                val response = appointmentEndpoint.getAppointById(id)
                val appointment = convertDtoToAppointment(response)
                appointmentDao.insertAppointment(appointment)
                appointment
            } catch (e: Exception) {
                appointmentDao.getAppointmentById(id)
            }
        }
    }

    // Creates an appointment with offline queue support
    suspend fun createAppointment(appointment: AppointmentRequest) {
        withContext(Dispatchers.IO) {
            try {
                // Try to create immediately if online
                val request = appointment.copy(
                    start_time = appointment.start_time.format(formatter),
                    end_time = appointment.end_time.format(formatter)
                )
                val response = appointmentEndpoint.addAppointment(request)

                // Cache the created appointment
                val createdAppointment = convertDtoToAppointment(response)
                appointmentDao.insertAppointment(createdAppointment)
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error creating appointment, queuing for sync", e)
                // If offline, store locally and schedule sync
                val localAppointment = Appointment(
                    id = 0, // Temporary ID for unsynced appointments
                    doctor_id = appointment.doctor,
                    patient_id = appointment.patient,
                    start_time = LocalDateTime.parse(appointment.start_time, formatter),
                    end_time = LocalDateTime.parse(appointment.end_time, formatter),
                    name = appointment.name,
                    gender = appointment.gender,
                    age = appointment.age,
                    problem_description = appointment.problem_description,
                    status = AppointmentStatus.CONFIRMED,
                    QR_code = "",
                    doctor_name = null,
                    doctor_specialty = null
                )
                appointmentDao.insertAppointment(localAppointment)
                scheduleSync()
                throw e // Re-throw to let UI handle the offline case
            }
        }
    }

    // Updates an appointment with offline support
    suspend fun updateAppointment(id: Int, appointment: AppointmentRequest) {
        withContext(Dispatchers.IO) {
            try {
                // Try to update immediately if online
                val request = appointment.copy(
                    start_time = appointment.start_time.format(formatter),
                    end_time = appointment.end_time.format(formatter)
                )
                appointmentEndpoint.updateAppointment(id, request)

                // Update local cache
                val updatedAppointment = convertDtoToAppointment(
                    AppointmentDto(
                        id = id,
                        doctor = appointment.doctor,
                        patient = appointment.patient,
                        start_time = appointment.start_time,
                        end_time = appointment.end_time,
                        name = appointment.name,
                        gender = appointment.gender,
                        age = appointment.age,
                        problem_description = appointment.problem_description,
                        status = "CONFIRMED",
                        qr_Code = "",
                        doctor_name = null,
                        doctor_specialty = null
                    )
                )
                appointmentDao.insertAppointment(updatedAppointment)
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error updating appointment, queuing for sync", e)
                // If offline, update locally and schedule sync
                val localAppointment = Appointment(
                    id = id,
                    doctor_id = appointment.doctor,
                    patient_id = appointment.patient,
                    start_time = LocalDateTime.parse(appointment.start_time, formatter),
                    end_time = LocalDateTime.parse(appointment.end_time, formatter),
                    name = appointment.name,
                    gender = appointment.gender,
                    age = appointment.age,
                    problem_description = appointment.problem_description,
                    status = AppointmentStatus.CONFIRMED,
                    QR_code = "",
                    doctor_name = null,
                    doctor_specialty = null
                )
                appointmentDao.insertAppointment(localAppointment)
                scheduleSync()
                throw e // Re-throw to let UI handle the offline case
            }
        }
    }

    // Deletes an appointment with offline support
    suspend fun deleteAppointment(id: Int) {
        withContext(Dispatchers.IO) {
            try {
                // Try to delete immediately if online
                appointmentEndpoint.deleteAppointment(id)
                appointmentDao.getAppointmentById(id)?.let {
                    appointmentDao.deleteAppointment(it)
                }
            } catch (e: Exception) {
                Log.e("AppointmentRepository", "Error deleting appointment, marking for deletion", e)
                // If offline, mark for deletion and schedule sync
                appointmentDao.getAppointmentById(id)?.let {
                    val markedForDeletion = it.copy(status = AppointmentStatus.valueOf("DELETED"))
                    appointmentDao.insertAppointment(markedForDeletion)
                    scheduleSync()
                }
                throw e // Re-throw to let UI handle the offline case
            }
        }
    }

    // Gets all appointments that need to be synced
    suspend fun getUnsyncedAppointments(): List<Appointment> {
        return withContext(Dispatchers.IO) {
            // Get appointments with temporary IDs (created offline)
            val unsyncedCreates = appointmentDao.getAppointmentsByPatient(0)

            // Get appointments marked for deletion
            val markedForDeletion = appointmentDao.getAppointmentsByStatus("DELETED")

            // Get other potentially modified appointments (you might need additional tracking)
            unsyncedCreates + markedForDeletion
        }
    }

    // Marks an appointment as synced
    suspend fun markAppointmentAsSynced(appointment: Appointment) {
        withContext(Dispatchers.IO) {
            appointmentDao.insertAppointment(appointment)
        }
    }

    // Schedules a sync with the server
    private fun scheduleSync() {
        syncRepository.scheduleSync()
    }

}