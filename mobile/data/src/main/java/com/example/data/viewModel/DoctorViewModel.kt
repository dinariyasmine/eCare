package com.example.data.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Doctor
import com.example.data.network.UpdateDoctorRequest
import com.example.data.repository.DoctorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DoctorViewModel(private val repository: DoctorRepository) : ViewModel() {

    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> get() = _selectedDoctor

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> get() = _doctors

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Original list of doctors (for reapplying filters)
    private var allDoctors = listOf<Doctor>()

    // Enhanced filter state
    private var nameQuery: String? = null
    private val specialtyFilters = mutableSetOf<String>()
    private var minRatingFilter: Float? = null
    private var patientCountRange: Pair<Int?, Int?>? = null

    // Fetch list of doctors
    fun getDoctorsFromApi() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                allDoctors = repository.getAllDoctors()
                _doctors.value = allDoctors
            } catch (e: Exception) {
                _error.value = "Error loading doctors: ${e.message}"
                allDoctors = emptyList()
                _doctors.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    // Load selected doctor's details
    fun loadDoctorDetails(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val doctor = withContext(Dispatchers.IO) {
                    repository.getDoctorDetailsById(doctorId)
                }
                _selectedDoctor.value = doctor
            } catch (e: Exception) {
                _error.value = "Error loading doctor: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Update doctor details
    fun updateDoctorOnServer(doctorId: Int, updatedFields: UpdateDoctorRequest) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                withContext(Dispatchers.IO) {
                    repository.updateDoctor(doctorId, updatedFields)
                }
                // Refresh the doctor data after update
                loadDoctorDetails(doctorId)
                _error.value = null
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Error updating doctor: ${e.message}", e)
                _error.value = "Failed to update doctor: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Enhanced Filter and Search Functions
    fun searchDoctorsByName(name: String) {
        nameQuery = if (name.isNotBlank()) name else null
        applyFilters()
    }

    fun updateSpecialtyFilter(specialty: String, selected: Boolean) {
        if (selected) {
            specialtyFilters.add(specialty)
        } else {
            specialtyFilters.remove(specialty)
        }
        applyFilters()
    }

    fun updateRatingFilter(minRating: Float) {
        minRatingFilter = if (minRating > 0) minRating else null
        applyFilters()
    }

    fun updatePatientCountFilter(minPatients: Int, maxPatients: Int) {
        patientCountRange = if (minPatients > 0 || maxPatients < 1000) {
            Pair(minPatients, maxPatients)
        } else {
            null
        }
        applyFilters()
    }

    fun resetFilters() {
        nameQuery = null
        specialtyFilters.clear()
        minRatingFilter = null
        patientCountRange = null
        _doctors.value = allDoctors
    }

    private fun applyFilters() {
        val filtered = allDoctors.filter { doctor ->
            // Name filter
            val matchesName = nameQuery?.let {
                doctor.name.contains(it, ignoreCase = true)
            } ?: true

            // Specialty filter - check if doctor's specialty is in our selected specialties set
            val matchesSpecialty = if (specialtyFilters.isEmpty()) {
                true
            } else {
                specialtyFilters.contains(doctor.specialty)
            }

            // Rating filter - Fixed the type mismatch error
            val doctorGrade = doctor.grade.toFloat()
            val matchesRating = minRatingFilter?.let { minRating ->
                doctorGrade >= minRating
            } ?: true

            // Patient count filter
            val matchesPatientCount = patientCountRange?.let { (min, max) ->
                val patientCount = doctor.nbr_patients
                (min == null || patientCount >= min) &&
                        (max == null || patientCount <= max)
            } ?: true

            matchesName && matchesSpecialty && matchesRating && matchesPatientCount
        }

        _doctors.value = filtered
    }

    // Utility method to get active filters count - useful for UI indicators
    fun getActiveFilterCount(): Int {
        var count = 0
        if (nameQuery != null) count++
        if (specialtyFilters.isNotEmpty()) count++
        if (minRatingFilter != null) count++
        if (patientCountRange != null) count++
        return count
    }

    // Debug method to log current filters
    fun logCurrentFilters() {
        Log.d("DoctorViewModel", "Current filters: " +
                "name=$nameQuery, " +
                "specialties=${specialtyFilters.joinToString()}, " +
                "minRating=$minRatingFilter, " +
                "patientRange=$patientCountRange")
    }

    companion object {
        class Factory(private val repository: DoctorRepository) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
                    return DoctorViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
