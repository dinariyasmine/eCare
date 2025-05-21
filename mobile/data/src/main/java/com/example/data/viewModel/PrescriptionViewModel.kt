package com.example.data.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Prescription
import com.example.data.repository.PrescriptionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrescriptionViewModel(
    private val repository: PrescriptionRepository
) : ViewModel() {

    private val _prescriptions = MutableStateFlow<List<Prescription>>(emptyList())
    val prescriptions: StateFlow<List<Prescription>> = _prescriptions

    private val _selectedPrescription = MutableStateFlow<Prescription?>(null)
    val selectedPrescription: StateFlow<Prescription?> = _selectedPrescription

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchPrescriptions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = repository.getPrescriptions()
                _prescriptions.value = result
                Log.d("PrescriptionViewModel", "Fetched ${result.size} prescriptions")
            } catch (e: Exception) {
                Log.e("PrescriptionViewModel", "Error fetching prescriptions: ${e.message}", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchPrescriptionById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("PrescriptionViewModel", "Fetching prescription with ID: $id")
                val result = repository.getPrescriptionById(id)
                _selectedPrescription.value = result
                Log.d("PrescriptionViewModel", "Prescription fetched successfully")
            } catch (e: Exception) {
                Log.e("PrescriptionViewModel", "Error fetching prescription: ${e.message}", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createPrescription(
        patientId: Int,
        doctorId: Int,
        date: String,
        notes: String,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("PrescriptionViewModel", "Creating prescription for patient $patientId with doctor $doctorId")
                val prescriptionId = repository.createPrescription(patientId, doctorId, date, notes)
                delay(10000)
                Log.d("PrescriptionViewModel", "Prescription created with ID: $prescriptionId")
                onSuccess(prescriptionId)
            } catch (e: Exception) {
                Log.e("PrescriptionViewModel", "Error creating prescription: ${e.message}", e)
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun addMedicationToPrescription(
        prescriptionId: Int,
        medicationId: Int,
        dosage: String,
        duration: String,
        frequency: String,
        instructions: String
    ) {
        viewModelScope.launch {
            try {
                Log.d("PrescriptionViewModel", "Adding medication $medicationId to prescription $prescriptionId")
                repository.addMedicationToPrescription(
                    prescriptionId, medicationId, dosage, duration, frequency, instructions
                )
                Log.d("PrescriptionViewModel", "Medication added successfully")
            } catch (e: Exception) {
                Log.e("PrescriptionViewModel", "Error adding medication: ${e.message}", e)
                // Handle error silently
            }
        }
    }

    fun generatePrescriptionPdf(
        prescriptionId: Int,
        onComplete: ((String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                Log.d("PrescriptionViewModel", "Generating PDF for prescription $prescriptionId")
                val pdfUrl = repository.generatePrescriptionPdf(prescriptionId)
                Log.d("PrescriptionViewModel", "PDF generated: $pdfUrl")
                onComplete?.invoke(pdfUrl)
            } catch (e: Exception) {
                Log.e("PrescriptionViewModel", "Error generating PDF: ${e.message}", e)
                onComplete?.invoke(null)
            }
        }
    }

    companion object {
        class Factory(private val repository: PrescriptionRepository) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PrescriptionViewModel::class.java)) {
                    return PrescriptionViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
