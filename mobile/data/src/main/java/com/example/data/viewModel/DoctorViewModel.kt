// First, let's update the DoctorViewModel to handle filtering functionality
package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Doctor
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorViewModel(
    private val doctorRepository: DoctorRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> get() = _doctors

    // Original unfiltered doctors list to reset filters
    private val _allDoctors = MutableStateFlow<List<Doctor>>(emptyList())

    // Current filter states
    private val _activeSpecialties = MutableStateFlow<Set<String>>(emptySet())
    private val _minRating = MutableStateFlow(0f)
    private val _locationFilter = MutableStateFlow<String?>(null)
    private val _patientCountRange = MutableStateFlow<Pair<Int?, Int?>>(null to null)

    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> get() = _selectedDoctor

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        fetchAllDoctors()
    }

    fun fetchAllDoctors() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val allDoctors = doctorRepository.getAllDoctors()
                _allDoctors.value = allDoctors
                _doctors.value = allDoctors
            } catch (e: Exception) {
                _error.value = "Failed to load doctors: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // New function to handle search and apply current filters
    fun searchDoctorsByName(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Filter based on search query
                var filtered = if (query.isBlank()) {
                    _allDoctors.value
                } else {
                    // In a real app, this would use the repository
                    // For now, we'll filter in memory since the repository doesn't support this directly
                    _allDoctors.value.filter { doctor ->
                        // Search by specialty or description
                        doctor.specialty.contains(query, ignoreCase = true) ||
                                doctor.description.contains(query, ignoreCase = true)
                    }
                }

                // Apply specialty filters
                if (_activeSpecialties.value.isNotEmpty()) {
                    filtered = filtered.filter { doctor ->
                        _activeSpecialties.value.contains(doctor.specialty)
                    }
                }

                // Apply rating filter
                if (_minRating.value > 0) {
                    filtered = filtered.filter { doctor ->
                        doctor.grade >= _minRating.value
                    }
                }

                // Apply location filter (simplified since we don't have location data)
                when (_locationFilter.value) {
                    "Near me" -> filtered = filtered.take(3) // Simplified: just take top 3
                    "My City" -> filtered = filtered.filter { it.clinic_id in 1..4 } // Simplified: clinics 1-4 in "my city"
                }

                // Apply patient count filter
                val (minPatients, maxPatients) = _patientCountRange.value
                if (minPatients != null || maxPatients != null) {
                    filtered = filtered.filter { doctor ->
                        val meetsMin = minPatients == null || doctor.nbr_patients >= minPatients
                        val meetsMax = maxPatients == null || doctor.nbr_patients <= maxPatients
                        meetsMin && meetsMax
                    }
                }

                _doctors.value = filtered
            } catch (e: Exception) {
                _error.value = "Failed to search doctors: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    fun updateDoctor(doctor: Doctor) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = doctorRepository.updateDoctor(doctor)
                if (success) {
                    fetchAllDoctors() // Refresh the list
                    _selectedDoctor.value = doctor // Update selected doctor if it was selected
                } else {
                    _error.value = "Failed to update doctor: Doctor not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update doctor: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }


    // Filter functions
    fun updateSpecialtyFilter(specialty: String, isSelected: Boolean) {
        val currentSpecialties = _activeSpecialties.value.toMutableSet()
        if (isSelected) {
            currentSpecialties.add(specialty)
        } else {
            currentSpecialties.remove(specialty)
        }
        _activeSpecialties.value = currentSpecialties
        applyAllFilters()
    }

    fun updateRatingFilter(minRating: Float) {
        _minRating.value = minRating
        applyAllFilters()
    }

    fun updateLocationFilter(location: String?) {
        _locationFilter.value = location
        applyAllFilters()
    }

    fun updatePatientCountFilter(minPatients: Int?, maxPatients: Int?) {
        _patientCountRange.value = minPatients to maxPatients
        applyAllFilters()
    }

    fun resetFilters() {
        _activeSpecialties.value = emptySet()
        _minRating.value = 0f
        _locationFilter.value = null
        _patientCountRange.value = null to null
        _doctors.value = _allDoctors.value
    }

    private fun applyAllFilters() {
        // Apply all active filters based on current search term
        searchDoctorsByName("")
    }

    fun filterBySymptom(symptom: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Map symptoms to specialties (simplified)
                val specialtyMap = mapOf(
                    "Headache" to "Neurology",
                    "Nausea" to "Gastroenterology",
                    "Fever" to "General Practice",
                    "Cold" to "General Practice",
                    "Palpitations" to "Cardiology"
                )

                val specialty = specialtyMap[symptom] ?: return@launch

                _doctors.value = _allDoctors.value.filter {
                    it.specialty == specialty
                }
            } catch (e: Exception) {
                _error.value = "Failed to filter by symptom: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // ... [rest of existing methods]

    // Factory class to provide dependencies
    class Factory(
        private val doctorRepository: DoctorRepository,
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
                return DoctorViewModel(doctorRepository, userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
