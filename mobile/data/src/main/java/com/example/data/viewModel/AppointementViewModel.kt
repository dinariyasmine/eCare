package com.example.data.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Appointment
import com.example.data.model.AppointmentRequest
import com.example.data.model.AppointmentStatus
import com.example.data.repository.AppointmentRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class AppointmentViewModel(
    private val repository: AppointmentRepository
) : ViewModel() {

    private val _appointments = MutableLiveData<List<Appointment>>()
    val appointments: LiveData<List<Appointment>> get() = _appointments

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> get() = _operationSuccess

    private val _currentAppointment = MutableLiveData<Appointment>()
    val currentAppointment: LiveData<Appointment> get() = _currentAppointment

    fun getAppointmentsByDoctor(doctorId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("AppointmentViewModel", "Fetching appointments for doctor ID: $doctorId")
                val appointments = repository.getAppointmentsByDoctor(doctorId)
                Log.d("AppointmentViewModel", "Received ${appointments.size} appointments for doctor")
                appointments.forEach { appointment ->
                    Log.d("AppointmentViewModel", """
                        Doctor Appointment Details:
                        ID: ${appointment.id}
                        Start Time: ${appointment.start_time}
                        End Time: ${appointment.end_time}
                        Status: ${appointment.status}
                        Patient ID: ${appointment.patient_id}
                    """.trimIndent())
                }
                _appointments.value = appointments
                _operationSuccess.value = true
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Error fetching doctor's appointments", e)
                _error.value = "Failed to fetch doctor's appointments: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAppointmentsForDate(date: LocalDate): List<Appointment> {
        val filteredAppointments = appointments.value?.filter { it.start_time.toLocalDate() == date }
            ?.sortedBy { it.start_time }
            ?: emptyList()
        Log.d("AppointmentViewModel", "Filtered ${filteredAppointments.size} appointments for date: $date")
        return filteredAppointments
    }

    fun getAppointmentsByPatient(patientId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("AppointmentViewModel", "Fetching appointments for patient ID: $patientId")
                val appointments = repository.getAppointmentsByPatient(patientId)
                Log.d("AppointmentViewModel", "Received ${appointments.size} appointments for patient")
                appointments.forEach { appointment ->
                    Log.d("AppointmentViewModel", """
                        Patient Appointment Details:
                        ID: ${appointment.id}
                        Start Time: ${appointment.start_time}
                        End Time: ${appointment.end_time}
                        Status: ${appointment.status}
                        Doctor ID: ${appointment.doctor_id}
                    """.trimIndent())
                }
                _appointments.value = appointments
                _operationSuccess.value = true
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Error fetching patient's appointments", e)
                _error.value = "Failed to fetch patient's appointments: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAppointmentById(appointmentId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val appointment = repository.getAppointmentById(appointmentId)
                _currentAppointment.value = appointment
                _operationSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Failed to fetch appointment: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createAppointment(request: AppointmentRequest) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("AppointmentViewModel", "Creating new appointment for patient: ${request.patient}")
                repository.createAppointment(request)
                Log.d("AppointmentViewModel", "Appointment created successfully")
                getAppointmentsByPatient(request.patient)
                _operationSuccess.value = true
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Error creating appointment", e)
                _error.value = "Failed to create appointment: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateAppointment(id: Int, appointment: Appointment) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("AppointmentViewModel", "Updating appointment ID: $id")
                repository.updateAppointment(id, appointment)
                Log.d("AppointmentViewModel", "Appointment updated successfully")
                if (appointment.patient_id != null) {
                    getAppointmentsByPatient(appointment.patient_id)
                } else if (appointment.doctor_id != null) {
                    getAppointmentsByDoctor(appointment.doctor_id)
                }
                _operationSuccess.value = true
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Error updating appointment", e)
                _error.value = "Failed to update appointment: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAppointment(id: Int, userId: Int, isPatient: Boolean) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("AppointmentViewModel", "Deleting appointment ID: $id")
                repository.deleteAppointment(id)
                Log.d("AppointmentViewModel", "Appointment deleted successfully")
                if (isPatient) {
                    getAppointmentsByPatient(userId)
                } else {
                    getAppointmentsByDoctor(userId)
                }
                _operationSuccess.value = true
            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "Error deleting appointment", e)
                _error.value = "Failed to delete appointment: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterAppointmentsByStatus(status: AppointmentStatus) {
        val currentList = _appointments.value ?: return
        val filteredList = currentList.filter { it.status == status }
        Log.d("AppointmentViewModel", "Filtered ${filteredList.size} appointments by status: $status")
        _appointments.value = filteredList
    }
}