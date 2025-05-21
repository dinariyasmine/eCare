package com.example.data.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Patient
import com.example.data.network.UpdatePatientRequest
import com.example.data.repository.PatientRepository
import com.example.data.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients

    private val _selectedPatient = MutableStateFlow<Patient?>(null)
    val selectedPatient: StateFlow<Patient?> = _selectedPatient

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Load all patients from the API
    fun getPatientsFromApi() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getAllPatients()
                _patients.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to fetch patients: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Load one patient by ID
    fun loadPatientById(patientId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getPatientById(patientId)
                _selectedPatient.value = result
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to fetch patient details: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadPatientDetails(patientId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val patient = withContext(Dispatchers.IO) {
                    repository.getPatientById(patientId)
                }
                _selectedPatient.value = patient ?: run {
                    _error.value = "Patient not found"
                    null
                }
            } catch (e: Exception) {
                _error.value = "Error loading patient: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Update selected patient (UI-only change)
    fun updatePatient(updatedPatient: Patient) {
        _selectedPatient.value = updatedPatient
    }

    // Search patients by name
    fun searchPatientsByName(query: String) {
        _patients.value = _patients.value.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    // Reset patient list
    fun resetPatients() {
        getPatientsFromApi()
    }

    // Update a patient on the backend
    fun updatePatientOnServer(patientId: Int, updatedFields: UpdatePatientRequest) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val message = withContext(Dispatchers.IO) {
                    repository.updatePatient(patientId, updatedFields)
                }
                // Optionally reload updated patient
                loadPatientById(patientId)
                _error.value = null
            } catch (e: Exception) {
                Log.e("PatientViewModel", "Error updating patient: ${e.message}", e)
                _error.value = "Failed to update patient: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    companion object {
        class Factory(private val repository: PatientRepository) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
                    return PatientViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
