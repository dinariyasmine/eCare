// In mobile/data/src/main/java/com/example/data/repository/MedicationRepository.kt
package com.example.data.repository

import com.example.data.model.Medication
import com.example.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MedicationRepository(private val apiService: ApiService) {

    suspend fun getMedications(): List<Medication> = withContext(Dispatchers.IO) {
        apiService.getMedications()
    }

    suspend fun getMedicationById(id: Int): Medication = withContext(Dispatchers.IO) {
        apiService.getMedicationById(id)
    }

    suspend fun createMedication(name: String, description: String? = null): Medication = withContext(Dispatchers.IO) {
        apiService.createMedication(
            mapOf(
                "name" to name,
                "description" to (description ?: "")
            )
        )
    }

    suspend fun addMedicationToPrescription(
        prescriptionId: Int,
        medicationId: Int,
        dosage: String,
        duration: String,
        frequency: String,
        instructions: String
    ) = withContext(Dispatchers.IO) {
        apiService.addMedicationToPrescription(
            prescriptionId,
            mapOf(
                "medication_id" to medicationId,
                "dosage" to dosage,
                "duration" to duration,
                "frequency" to frequency,
                "instructions" to instructions
            )
        )
    }

}
