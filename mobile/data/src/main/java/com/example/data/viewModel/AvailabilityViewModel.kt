package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Availability
import com.example.data.model.AvailabilityRequest
import com.example.data.repository.AvailabilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AvailabilityViewModel(private val availabilityRepository: AvailabilityRepository, private val doctorId: Int) : ViewModel() {

    private val _availabilities = MutableStateFlow<List<Availability>>(emptyList())
    val availabilities: StateFlow<List<Availability>> get() = _availabilities

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Initialize by fetching all availabilities of the doctor
    init {
        fetchAllAvailabilities(doctorId)
    }

    fun fetchAllAvailabilities(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                println("Fetching availabilities for doctor ID: $id")
                _availabilities.value = availabilityRepository.getAvailabilitiesByDoctor(id)
                println("Fetched: ${availabilities.value.size} availabilities")
            } catch (e: Exception) {
                println("Error: ${e.message}")
                _error.value = "Failed to load availabilities: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createAvailability(startTime: Date, endTime: Date) {
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

                val newAvailability = AvailabilityRequest(
                    doctor_id = doctorId,
                    start_time = startTime,
                    end_time = endTime
                )

                val success = availabilityRepository.createAvailability(newAvailability)
                if (success) {
                    fetchAllAvailabilities(doctorId) // Refresh the list
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

                val success = availabilityRepository.updateAvailability(availability.id, availability)
                if (success) {
                    fetchAllAvailabilities(doctorId)
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
                    fetchAllAvailabilities(doctorId) // Refresh the list
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
}
