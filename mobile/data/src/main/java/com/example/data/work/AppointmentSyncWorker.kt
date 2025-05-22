package com.example.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.model.Appointment
import com.example.data.model.AppointmentRequest
import com.example.data.repository.AppointmentRepository
import com.example.data.retrofit.AppointmentEndpoint
import com.example.data.room.AppointmentDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class AppointmentSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val appointmentDao: AppointmentDao,
    private val appointmentEndpoint: AppointmentEndpoint
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Get all unsynced appointments
                val unsyncedCreates = appointmentDao.getAppointmentsByPatient(0)
                val markedForDeletion = appointmentDao.getAppointmentsByStatus("DELETED")

                // Process creations first
                unsyncedCreates.forEach { appointment ->
                    try {
                        val request = AppointmentRequest(
                            doctor = appointment.doctor_id,
                            patient = appointment.patient_id,
                            start_time = appointment.start_time.toString(),
                            end_time = appointment.end_time.toString(),
                            name = appointment.name,
                            gender = appointment.gender,
                            age = appointment.age,
                            problem_description = appointment.problem_description
                        )

                        // Create on server
                        val response = appointmentEndpoint.addAppointment(request)

                        // Update local record with server ID and remove temp ID
                        val syncedAppointment = appointment.copy(id = response.id)
                        appointmentDao.insertAppointment(syncedAppointment)
                    } catch (e: Exception) {
                        // If sync fails for one, continue with others
                        // This appointment will be retried in next sync
                    }
                }

                // Process deletions
                markedForDeletion.forEach { appointment ->
                    try {
                        // Delete on server
                        appointmentEndpoint.deleteAppointment(appointment.id)

                        // Remove from local DB
                        appointmentDao.deleteAppointment(appointment)
                    } catch (e: Exception) {
                        // If sync fails for one, continue with others
                        // This appointment will be retried in next sync
                    }
                }

                // Check if there are any remaining unsynced changes
                val remainingUnsynced = appointmentDao.getAppointmentsByPatient(0).isNotEmpty() ||
                        appointmentDao.getAppointmentsByStatus("DELETED").isNotEmpty()

                if (remainingUnsynced) {
                    // If some changes couldn't be synced, retry later
                    Result.retry()
                } else {
                    // All changes synced successfully
                    Result.success()
                }
            } catch (e: Exception) {
                // General failure, retry later
                Result.retry()
            }
        }
    }
}