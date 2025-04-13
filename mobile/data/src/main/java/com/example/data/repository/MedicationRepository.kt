package com.example.data.repository

import com.example.data.model.Medication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MedicationRepository {
    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Medication> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        return@withContext listOf(
            Medication(
                id = 1,
                name = "Aspirin",
                dosage = "500 mg",
                frequency = "Once daily",
                instructions = "Take with food",
                prescription_id = 101
            ),
            Medication(
                id = 2,
                name = "Lisinopril",
                dosage = "10 mg",
                frequency = "Once daily",
                instructions = "Take in the morning",
                prescription_id = 102
            ),
            Medication(
                id = 3,
                name = "Metformin",
                dosage = "850 mg",
                frequency = "Twice daily",
                instructions = "Take with meals",
                prescription_id = 103
            )
        )
    }

    suspend fun getAllMedications(): List<Medication> {
        return simulateDatabaseCall()
    }

    suspend fun getMedicationById(id: Int): Medication? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun getMedicationsByPrescriptionId(prescriptionId: Int): List<Medication> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.prescription_id == prescriptionId }
    }

    suspend fun createMedication(medication: Medication): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        return !simulateDatabaseCall().any { it.id == medication.id }
    }

    suspend fun updateMedication(medication: Medication): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == medication.id }
    }

    suspend fun deleteMedication(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }

    suspend fun searchMedicationsByName(query: String): List<Medication> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.name.contains(query, ignoreCase = true) }
    }
}
