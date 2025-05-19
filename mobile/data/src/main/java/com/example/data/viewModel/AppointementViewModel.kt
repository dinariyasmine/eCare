package com.example.data.viewModel

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

    fun getAppointmentsByDoctor(doctorId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val appointments = repository.getAppointmentsByDoctor(doctorId)
                _appointments.value = appointments
                _operationSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Failed to fetch doctor's appointments: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAppointmentsForDate(date: LocalDate): List<Appointment> {
        return appointments.value!!.filter { it.date == date }
            .sortedBy { it.start_time }
    }

    fun getAppointmentsByPatient(patientId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val appointments = repository.getAppointmentsByPatient(patientId)
                _appointments.value = appointments
                _operationSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Failed to fetch patient's appointments: ${e.message}"
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
                repository.createAppointment(request)
                // Refresh the list after creation
                if (request.patient_id != null) {
                    getAppointmentsByPatient(request.patient_id)
                } else if (request.doctor_id != null) {
                    getAppointmentsByDoctor(request.doctor_id)
                }
                _operationSuccess.value = true
            } catch (e: Exception) {
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
                repository.updateAppointment(id, appointment)
                // Refresh the list after update
                if (appointment.patient_id != null) {
                    getAppointmentsByPatient(appointment.patient_id)
                } else if (appointment.doctor_id != null) {
                    getAppointmentsByDoctor(appointment.doctor_id)
                }
                _operationSuccess.value = true
            } catch (e: Exception) {
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
                repository.deleteAppointment(id)
                // Refresh the list after deletion
                if (isPatient) {
                    getAppointmentsByPatient(userId)
                } else {
                    getAppointmentsByDoctor(userId)
                }
                _operationSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Failed to delete appointment: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterAppointmentsByStatus(status: AppointmentStatus) {
        val currentList = _appointments.value ?: return
        _appointments.value = currentList.filter { it.status == status }
    }
}