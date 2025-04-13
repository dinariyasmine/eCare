package com.example.data.repository

import com.example.data.model.Patient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PatientRepository {
    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Patient> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        return@withContext listOf(
            Patient(
                id = 1,
                user_id = 201
            ),
            Patient(
                id = 2,
                user_id = 202
            ),
            Patient(
                id = 3,
                user_id = 203
            )
        )
    }

    suspend fun getAllPatients(): List<Patient> {
        return simulateDatabaseCall()
    }

    suspend fun getPatientById(id: Int): Patient? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun getPatientByUserId(userId: Int): Patient? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.user_id == userId }
    }

    suspend fun createPatient(patient: Patient): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        val existingPatients = simulateDatabaseCall()
        return !existingPatients.any { it.id == patient.id || it.user_id == patient.user_id }
    }

    suspend fun updatePatient(patient: Patient): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == patient.id }
    }

    suspend fun deletePatient(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }
}
