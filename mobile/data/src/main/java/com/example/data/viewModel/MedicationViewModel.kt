package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Medication
import com.example.data.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicationViewModel(private val medicationRepository: MedicationRepository) : ViewModel() {

    private val _medications = MutableStateFlow<List<Medication>>(emptyList())
    val medications: StateFlow<List<Medication>> get() = _medications

    private val _selectedMedication = MutableStateFlow<Medication?>(null)
    val selectedMedication: StateFlow<Medication?> get() = _selectedMedication

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        fetchAllMedications()
    }

    fun fetchAllMedications() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _medications.value = medicationRepository.getAllMedications()
            } catch (e: Exception) {
                _error.value = "Failed to load medications: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getMedicationById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val medication = medicationRepository.getMedicationById(id)
                if (medication != null) {
                    _selectedMedication.value = medication
                } else {
                    _error.value = "Medication not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get medication: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getMedicationsByPrescriptionId(prescriptionId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _medications.value = medicationRepository.getMedicationsByPrescriptionId(prescriptionId)
            } catch (e: Exception) {
                _error.value = "Failed to load medications for prescription: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createMedication(
        name: String,
        dosage: String,
        frequency: String,
        instructions: String,
        prescriptionId: Int
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate input
                if (name.isBlank()) {
                    _error.value = "Medication name cannot be empty"
                    _loading.value = false
                    return@launch
                }

                if (dosage.isBlank()) {
                    _error.value = "Dosage cannot be empty"
                    _loading.value = false
                    return@launch
                }

                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_medications.value.maxOfOrNull { it.id } ?: 0) + 1

                val newMedication = Medication(
                    id = newId,
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    instructions = instructions,
                    prescription_id = prescriptionId
                )

                val success = medicationRepository.createMedication(newMedication)
                if (success) {
                    fetchAllMedications() // Refresh the list
                } else {
                    _error.value = "Failed to create medication: Medication with the same ID already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create medication: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateMedication(medication: Medication) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate input
                if (medication.name.isBlank()) {
                    _error.value = "Medication name cannot be empty"
                    _loading.value = false
                    return@launch
                }

                if (medication.dosage.isBlank()) {
                    _error.value = "Dosage cannot be empty"
                    _loading.value = false
                    return@launch
                }

                val success = medicationRepository.updateMedication(medication)
                if (success) {
                    fetchAllMedications() // Refresh the list
                    if (_selectedMedication.value?.id == medication.id) {
                        _selectedMedication.value = medication // Update selected medication if it was selected
                    }
                } else {
                    _error.value = "Failed to update medication: Medication not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update medication: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteMedication(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = medicationRepository.deleteMedication(id)
                if (success) {
                    fetchAllMedications() // Refresh the list
                    if (_selectedMedication.value?.id == id) {
                        _selectedMedication.value = null // Clear selected medication if it was deleted
                    }
                } else {
                    _error.value = "Failed to delete medication: Medication not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete medication: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchMedicationsByName(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _medications.value = medicationRepository.searchMedicationsByName(query)
            } catch (e: Exception) {
                _error.value = "Failed to search medications by name: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedMedication() {
        _selectedMedication.value = null
    }

    // Factory class to provide MedicationRepository dependency
    class Factory(private val medicationRepository: MedicationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MedicationViewModel::class.java)) {
                return MedicationViewModel(medicationRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
