package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Doctor
import com.example.data.model.DoctorDetails
import com.example.data.repository.DoctorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DoctorViewModel : ViewModel() {

    // --- New ---
    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> get() = _selectedDoctor

    // Existing StateFlows
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> get() = _doctors

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _specialtyFilters = MutableStateFlow<Set<String>>(emptySet())
    val specialtyFilters: StateFlow<Set<String>> get() = _specialtyFilters

    private val _ratingFilter = MutableStateFlow<Float?>(null)
    val ratingFilter: StateFlow<Float?> get() = _ratingFilter

    private val _locationFilter = MutableStateFlow<String?>(null)
    val locationFilter: StateFlow<String?> get() = _locationFilter

    private val _patientCountFilter = MutableStateFlow<Pair<Int, Int>?>(null)
    val patientCountFilter: StateFlow<Pair<Int, Int>?> get() = _patientCountFilter

    private val _symptomFilters = MutableStateFlow<Set<String>>(emptySet())
    val symptomFilters: StateFlow<Set<String>> get() = _symptomFilters

    fun fetchAllDoctors() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val doctorList = getDoctorsFromApi()
                _doctors.value = doctorList

                // Set first doctor as selected (customize this logic if needed)
                if (doctorList.isNotEmpty()) {
                    _selectedDoctor.value = doctorList.first()
                }

            } catch (e: Exception) {
                _error.value = "Failed to fetch doctors: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun getDoctorsFromApi(): List<Doctor> {
        return withContext(Dispatchers.IO) {
            listOf(
                Doctor(1, 101, "url_to_photo_1", "Cardiology", 201, 4.5f, "Experienced cardiologist", 120),
                Doctor(2, 102, "url_to_photo_2", "Dermatology", 202, 4.2f, "Expert in skin care", 98)
            )
        }
    }
    private val _doctorDetails = MutableStateFlow<DoctorDetails?>(null)
    val doctorDetails: StateFlow<DoctorDetails?> get() = _doctorDetails

    private val repository = DoctorRepository()

    fun loadDoctorDetails(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val details = withContext(Dispatchers.IO) {
                    repository.getDoctorDetailsById(doctorId)
                }
                if (details != null) {
                    _doctorDetails.value = details
                } else {
                    _error.value = "Doctor not found"
                }
            } catch (e: Exception) {
                _error.value = "Error loading doctor: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }


    private fun getDoctorDetailsFromApi(doctorId: Int): Doctor? {
return null
    }

    // --- New ---
    fun updateDoctor(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        birthday: String
    ) {
        viewModelScope.launch {
            val current = _selectedDoctor.value
            if (current != null) {
                val updated = current.copy(

                )
                _selectedDoctor.value = updated
            } else {
                _error.value = "No doctor selected"
            }
        }
    }

    fun searchDoctorsByName(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val filteredDoctors = _doctors.value.filter {
                    it.specialty.contains(query, ignoreCase = true)
                }
                _doctors.value = filteredDoctors
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun filterBySymptom(symptom: String) {
        val currentSymptoms = _symptomFilters.value.toMutableSet()
        if (currentSymptoms.contains(symptom)) {
            currentSymptoms.remove(symptom)
        } else {
            currentSymptoms.add(symptom)
        }
        _symptomFilters.value = currentSymptoms
    }

    fun updateSpecialtyFilter(specialty: String, isSelected: Boolean) {
        val currentSpecialties = _specialtyFilters.value.toMutableSet()
        if (isSelected) {
            currentSpecialties.add(specialty)
        } else {
            currentSpecialties.remove(specialty)
        }
        _specialtyFilters.value = currentSpecialties
    }

    fun updateRatingFilter(minRating: Float) {
        _ratingFilter.value = minRating
    }

    fun updateLocationFilter(location: String) {
        _locationFilter.value = location
    }

    fun updatePatientCountFilter(minPatients: Int, maxPatients: Int) {
        _patientCountFilter.value = Pair(minPatients, maxPatients)
    }

    fun resetFilters() {
        _specialtyFilters.value = emptySet()
        _ratingFilter.value = null
        _locationFilter.value = null
        _patientCountFilter.value = null
        _symptomFilters.value = emptySet()
    }
}
