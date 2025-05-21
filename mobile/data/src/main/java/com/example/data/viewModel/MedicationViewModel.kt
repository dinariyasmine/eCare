package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Medication
import com.example.data.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicationViewModel(private val repository: MedicationRepository) : ViewModel() {

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> = _medications

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchMedications() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getMedications()
                _medications.value = result
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMedicationById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val medication = repository.getMedicationById(id)
                // Handle the medication as needed
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createMedication(name: String, description: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Call the correct repository method with the right parameters
                repository.createMedication(name, description)

                // Then refresh the list
                fetchMedications()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add this method to handle adding medications to prescriptions
    fun addMedicationToPrescription(
        prescriptionId: Int,
        medicationId: Int,
        dosage: String,
        duration: String,
        frequency: String,
        instructions: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.addMedicationToPrescription(
                    prescriptionId,
                    medicationId,
                    dosage,
                    duration,
                    frequency,
                    instructions
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        class Factory(private val repository: MedicationRepository) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
                    return MedicationViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
