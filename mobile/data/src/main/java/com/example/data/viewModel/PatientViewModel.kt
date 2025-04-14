package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Patient
import com.example.data.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientViewModel(private val patientRepository: PatientRepository) : ViewModel() {

    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> get() = _patients

    private val _selectedPatient = MutableStateFlow<Patient?>(null)
    val selectedPatient: StateFlow<Patient?> get() = _selectedPatient

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        fetchAllPatients()
    }

    fun fetchAllPatients() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _patients.value = patientRepository.getAllPatients()
            } catch (e: Exception) {
                _error.value = "Failed to load patients: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getPatientById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val patient = patientRepository.getPatientById(id)
                if (patient != null) {
                    _selectedPatient.value = patient
                } else {
                    _error.value = "Patient not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get patient: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getPatientByUserId(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val patient = patientRepository.getPatientByUserId(userId)
                if (patient != null) {
                    _selectedPatient.value = patient
                    _patients.value = listOf(patient)
                } else {
                    _error.value = "Patient not found for this user"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get patient by user ID: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createPatient(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_patients.value.maxOfOrNull { it.id } ?: 0) + 1

                val newPatient = Patient(
                    id = newId,
                    user_id = userId
                )

                val success = patientRepository.createPatient(newPatient)
                if (success) {
                    fetchAllPatients() // Refresh the list
                } else {
                    _error.value = "Failed to create patient: Patient with the same ID or user ID already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create patient: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updatePatient(patient: Patient) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = patientRepository.updatePatient(patient)
                if (success) {
                    fetchAllPatients() // Refresh the list
                    if (_selectedPatient.value?.id == patient.id) {
                        _selectedPatient.value = patient // Update selected patient if it was selected
                    }
                } else {
                    _error.value = "Failed to update patient: Patient not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update patient: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deletePatient(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = patientRepository.deletePatient(id)
                if (success) {
                    fetchAllPatients() // Refresh the list
                    if (_selectedPatient.value?.id == id) {
                        _selectedPatient.value = null // Clear selected patient if it was deleted
                    }
                } else {
                    _error.value = "Failed to delete patient: Patient not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete patient: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedPatient() {
        _selectedPatient.value = null
    }

    // Factory class to provide PatientRepository dependency
    class Factory(private val patientRepository: PatientRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
                return PatientViewModel(patientRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
