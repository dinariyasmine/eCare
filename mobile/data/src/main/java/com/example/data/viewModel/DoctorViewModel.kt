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
                _doctors.value = doctorRepository.getAllDoctors()
            } catch (e: Exception) {
                _error.value = "Failed to load doctors: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getDoctorById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val doctor = doctorRepository.getDoctorById(id)
                if (doctor != null) {
                    _selectedDoctor.value = doctor
                } else {
                    _error.value = "Doctor not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get doctor: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getDoctorByUserId(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val doctor = doctorRepository.getDoctorByUserId(userId)
                if (doctor != null) {
                    _selectedDoctor.value = doctor
                    _doctors.value = listOf(doctor)
                } else {
                    _error.value = "Doctor not found for this user"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get doctor by user ID: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getDoctorsByClinicId(clinicId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _doctors.value = doctorRepository.getDoctorsByClinicId(clinicId)
            } catch (e: Exception) {
                _error.value = "Failed to load doctors for clinic: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getDoctorsBySpecialty(specialty: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _doctors.value = doctorRepository.getDoctorsBySpecialty(specialty)
            } catch (e: Exception) {
                _error.value = "Failed to load doctors by specialty: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createDoctor(
        userId: Int,
        photo: String,
        specialty: String,
        clinicId: Int,
        description: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_doctors.value.maxOfOrNull { it.id } ?: 0) + 1

                val newDoctor = Doctor(
                    id = newId,
                    user_id = userId,
                    photo = photo,
                    specialty = specialty,
                    clinic_id = clinicId,
                    grade = 0.0f, // New doctors start with no rating
                    description = description,
                    nbr_patients = 0 // New doctors start with no patients
                )

                val success = doctorRepository.createDoctor(newDoctor)
                if (success) {
                    fetchAllDoctors() // Refresh the list
                } else {
                    _error.value = "Failed to create doctor: Doctor with the same ID or user ID already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create doctor: ${e.message}"
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

    fun deleteDoctor(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = doctorRepository.deleteDoctor(id)
                if (success) {
                    fetchAllDoctors() // Refresh the list
                    if (_selectedDoctor.value?.id == id) {
                        _selectedDoctor.value = null // Clear selected doctor if it was deleted
                    }
                } else {
                    _error.value = "Failed to delete doctor: Doctor not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete doctor: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchDoctorsByName(name: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _doctors.value = doctorRepository.searchDoctorsByName(name, userRepository)
            } catch (e: Exception) {
                _error.value = "Failed to search doctors by name: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getTopRatedDoctors(limit: Int = 5) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _doctors.value = doctorRepository.getTopRatedDoctors(limit)
            } catch (e: Exception) {
                _error.value = "Failed to load top rated doctors: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getMostExperiencedDoctors(limit: Int = 5) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _doctors.value = doctorRepository.getMostExperiencedDoctors(limit)
            } catch (e: Exception) {
                _error.value = "Failed to load most experienced doctors: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedDoctor() {
        _selectedDoctor.value = null
    }

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
