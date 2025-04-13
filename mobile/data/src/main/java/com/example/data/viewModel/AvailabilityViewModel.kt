package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Availability
import com.example.data.repository.AvailabilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class AvailabilityViewModel(private val availabilityRepository: AvailabilityRepository) : ViewModel() {

    private val _availabilities = MutableStateFlow<List<Availability>>(emptyList())
    val availabilities: StateFlow<List<Availability>> get() = _availabilities

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Initialize by fetching all availabilities
    init {
        fetchAllAvailabilities()
    }

    fun fetchAllAvailabilities() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _availabilities.value = availabilityRepository.getAllAvailabilities()
            } catch (e: Exception) {
                _error.value = "Failed to load availabilities: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getAvailabilityById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val availability = availabilityRepository.getAvailabilityById(id)
                if (availability != null) {
                    _availabilities.value = listOf(availability)
                } else {
                    _error.value = "Availability not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get availability: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getAvailabilitiesByDoctorId(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _availabilities.value = availabilityRepository.getAvailabilitiesByDoctorId(doctorId)
            } catch (e: Exception) {
                _error.value = "Failed to load doctor availabilities: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getAvailabilitiesByDateRange(startDateString: String, endDateString: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val startDate = sdf.parse(startDateString)
                val endDate = sdf.parse(endDateString)

                if (startDate != null && endDate != null) {
                    _availabilities.value = availabilityRepository.getAvailabilitiesByDateRange(startDate, endDate)
                } else {
                    _error.value = "Invalid date format"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load availabilities by date range: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createAvailability(doctorId: Int, startTime: Date, endTime: Date) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate time range
                if (startTime >= endTime) {
                    _error.value = "Start time must be before end time"
                    _loading.value = false
                    return@launch
                }

                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_availabilities.value.maxOfOrNull { it.id } ?: 0) + 1

                val newAvailability = Availability(
                    id = newId,
                    doctor_id = doctorId,
                    start_time = startTime,
                    end_time = endTime
                )

                val success = availabilityRepository.createAvailability(newAvailability)
                if (success) {
                    fetchAllAvailabilities() // Refresh the list
                } else {
                    _error.value = "Failed to create availability: Time slot overlaps with existing availability"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create availability: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateAvailability(availability: Availability) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate time range
                if (availability.start_time >= availability.end_time) {
                    _error.value = "Start time must be before end time"
                    _loading.value = false
                    return@launch
                }

                val success = availabilityRepository.updateAvailability(availability)
                if (success) {
                    fetchAllAvailabilities() // Refresh the list
                } else {
                    _error.value = "Failed to update availability: Time slot overlaps with existing availability"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update availability: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteAvailability(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = availabilityRepository.deleteAvailability(id)
                if (success) {
                    fetchAllAvailabilities() // Refresh the list
                } else {
                    _error.value = "Failed to delete availability"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete availability: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Factory class to provide AvailabilityRepository dependency
    class Factory(private val availabilityRepository: AvailabilityRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AvailabilityViewModel::class.java)) {
                return AvailabilityViewModel(availabilityRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
