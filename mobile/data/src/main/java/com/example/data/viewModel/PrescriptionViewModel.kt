package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Prescription
import com.example.data.repository.PrescriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PrescriptionViewModel(private val prescriptionRepository: PrescriptionRepository) : ViewModel() {

    private val _prescriptions = MutableStateFlow<List<Prescription>>(emptyList())
    val prescriptions: StateFlow<List<Prescription>> get() = _prescriptions

    private val _selectedPrescription = MutableStateFlow<Prescription?>(null)
    val selectedPrescription: StateFlow<Prescription?> get() = _selectedPrescription

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    init {
        fetchAllPrescriptions()
    }

    fun fetchAllPrescriptions() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _prescriptions.value = prescriptionRepository.getAllPrescriptions()
            } catch (e: Exception) {
                _error.value = "Failed to load prescriptions: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getPrescriptionById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val prescription = prescriptionRepository.getPrescriptionById(id)
                if (prescription != null) {
                    _selectedPrescription.value = prescription
                } else {
                    _error.value = "Prescription not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get prescription: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getPrescriptionsByPatientId(patientId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _prescriptions.value = prescriptionRepository.getPrescriptionsByPatientId(patientId)
            } catch (e: Exception) {
                _error.value = "Failed to load patient prescriptions: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getPrescriptionsByDoctorId(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _prescriptions.value = prescriptionRepository.getPrescriptionsByDoctorId(doctorId)
            } catch (e: Exception) {
                _error.value = "Failed to load doctor prescriptions: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getPrescriptionsByDate(dateString: String) {
        viewModelScope.launch {
            try {
                val date = sdf.parse(dateString)
                if (date != null) {
                    _prescriptions.value = prescriptionRepository.getPrescriptionsByDate(date)
                } else {
                    _error.value = "Invalid date format"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load prescriptions by date: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createPrescription(
        patientId: Int,
        doctorId: Int,
        dateString: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val date = sdf.parse(dateString)
                if (date == null) {
                    _error.value = "Invalid date format"
                    _loading.value = false
                    return@launch
                }

                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_prescriptions.value.maxOfOrNull { it.id } ?: 0) + 1

                val newPrescription = Prescription(
                    id = newId,
                    patient_id = patientId,
                    doctor_id = doctorId,
                    date = date
                )

                val success = prescriptionRepository.createPrescription(newPrescription)
                if (success) {
                    fetchAllPrescriptions() // Refresh the list
                } else {
                    _error.value =
                        "Failed to create prescription: Prescription with the same ID already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create prescription: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updatePrescription(prescription: Prescription) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = prescriptionRepository.updatePrescription(prescription)
                if (success) {
                    fetchAllPrescriptions() // Refresh the list
                    if (_selectedPrescription.value?.id == prescription.id) {
                        _selectedPrescription.value =
                            prescription // Update selected prescription if it was selected
                    }
                } else {
                    _error.value = "Failed to update prescription: Prescription not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update prescription: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deletePrescription(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = prescriptionRepository.deletePrescription(id)
                if (success) {
                    fetchAllPrescriptions() // Refresh the list
                    if (_selectedPrescription.value?.id == id) {
                        _selectedPrescription.value =
                            null // Clear selected prescription if it was deleted
                    }
                } else {
                    _error.value = "Failed to delete prescription: Prescription not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete prescription: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedPrescription() {
        _selectedPrescription.value = null
    }

    // Factory class to provide PrescriptionRepository dependency
    class Factory(private val prescriptionRepository: PrescriptionRepository) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PrescriptionViewModel::class.java)) {
                return PrescriptionViewModel(prescriptionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
