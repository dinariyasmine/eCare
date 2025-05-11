package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Doctor
import com.example.data.repository.DoctorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DoctorViewModel : ViewModel() {

    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> get() = _selectedDoctor

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> get() = _doctors

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val repository = DoctorRepository()

    // Filter state
    private var nameQuery: String? = null
    private var specialtyFilter: String? = null
    private var ratingFilter: Double? = null
    private var patientCountFilter: Int? = null

    // Fetch list of doctors
    suspend fun getDoctorsFromApi(): List<Doctor> {
        val allDoctors = repository.getAllDoctors()
        _doctors.value = allDoctors
        return allDoctors
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
                _selectedDoctor.value = doctor ?: run {
                    _error.value = "Doctor not found"
                    null
                }
            } catch (e: Exception) {
                _error.value = "Error loading doctor: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Update doctor details
    fun updateDoctor(firstName: String, lastName: String, email: String, phone: String, birthday: String) {
        viewModelScope.launch {
            val current = _selectedDoctor.value
            if (current != null) {
                val updated = current.copy(name = firstName, email = email, phone = phone, birth_date = birthday)
                _selectedDoctor.value = updated
            } else {
                _error.value = "No doctor selected"
            }
        }
    }

    // Filter and Search Functions

    fun searchDoctorsByName(name: String) {
        nameQuery = name
        applyFilters()
    }

    fun updateSpecialtyFilter(specialty: String?) {
        specialtyFilter = specialty
        applyFilters()
    }

    fun updateRatingFilter(minRating: Double?) {
        ratingFilter = minRating
        applyFilters()
    }

    fun updatePatientCountFilter(minPatients: Int?) {
        patientCountFilter = minPatients
        applyFilters()
    }

    fun resetFilters() {
        nameQuery = null
        specialtyFilter = null
        ratingFilter = null
        patientCountFilter = null
        viewModelScope.launch {
            _doctors.value = repository.getAllDoctors()
        }
    }

    private fun applyFilters() {
        viewModelScope.launch {
            val allDoctors = repository.getAllDoctors()
            val filtered = allDoctors.filter { doctor ->
                val matchesName = nameQuery?.let { doctor.name.contains(it, ignoreCase = true) } ?: true
                val matchesSpecialty = specialtyFilter?.let { doctor.specialty.equals(it, ignoreCase = true) } ?: true
                val matchesRating = ratingFilter?.let { doctor.grade >= it } ?: true
                val matchesPatientCount = patientCountFilter?.let { doctor.nbr_patients >= it } ?: true
                matchesName && matchesSpecialty && matchesRating && matchesPatientCount
            }
            _doctors.value = filtered
        }
    }
}
