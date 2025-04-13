package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AppointmentViewModel(private val appointmentRepository: AppointmentRepository) : ViewModel() {

    // State flows for UI state management
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> get() = _appointments

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Initialize by fetching all appointments
    init {
        fetchAllAppointments()
    }

    fun fetchAllAppointments() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _appointments.value = appointmentRepository.getAllAppointments()
            } catch (e: Exception) {
                _error.value = "Failed to load appointments: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getAppointmentById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val appointment = appointmentRepository.getAppointmentById(id)
                if (appointment != null) {
                    _appointments.value = listOf(appointment)
                } else {
                    _error.value = "Appointment not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get appointment: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createAppointment(
        doctorId: Int,
        patientId: Int,
        startTime: Date,
        endTime: Date,
        qrCode: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_appointments.value.maxOfOrNull { it.id } ?: 0) + 1

                val newAppointment = Appointment(
                    id = newId,
                    doctor_id = doctorId,
                    patient_id = patientId,
                    start_time = startTime,
                    end_time = endTime,
                    status = AppointmentStatus.CONFIRMED,
                    QR_code = qrCode
                )

                val success = appointmentRepository.createAppointment(newAppointment)
                if (success) {
                    fetchAllAppointments() // Refresh the list
                } else {
                    _error.value = "Failed to create appointment"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create appointment: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = appointmentRepository.updateAppointment(appointment)
                if (success) {
                    fetchAllAppointments() // Refresh the list
                } else {
                    _error.value = "Failed to update appointment"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update appointment: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateAppointmentStatus(id: Int, status: AppointmentStatus) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val appointment = appointmentRepository.getAppointmentById(id)
                if (appointment != null) {
                    val updatedAppointment = appointment.copy(status = status)
                    val success = appointmentRepository.updateAppointment(updatedAppointment)
                    if (success) {
                        fetchAllAppointments() // Refresh the list
                    } else {
                        _error.value = "Failed to update appointment status"
                    }
                } else {
                    _error.value = "Appointment not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update appointment status: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteAppointment(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = appointmentRepository.deleteAppointment(id)
                if (success) {
                    fetchAllAppointments() // Refresh the list
                } else {
                    _error.value = "Failed to delete appointment"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete appointment: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getAppointmentsByDoctorId(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _appointments.value = appointmentRepository.getAppointmentsByDoctorId(doctorId)
            } catch (e: Exception) {
                _error.value = "Failed to load doctor appointments: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getAppointmentsByPatientId(patientId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _appointments.value = appointmentRepository.getAppointmentsByPatientId(patientId)
            } catch (e: Exception) {
                _error.value = "Failed to load patient appointments: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getAppointmentsByStatus(status: AppointmentStatus) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _appointments.value = appointmentRepository.getAppointmentsByStatus(status)
            } catch (e: Exception) {
                _error.value = "Failed to load appointments by status: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Factory class to provide AppointmentRepository dependency
    class Factory(private val appointmentRepository: AppointmentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
                return AppointmentViewModel(appointmentRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
