package com.example.data.repository

import com.example.data.model.Clinic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ClinicRepository {
    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Clinic> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        return@withContext listOf(
            Clinic(
                id = 1,
                name = "Springfield General Hospital",
                adress = "123 Main St, Springfield, USA",
                map_location = "https://maps.example.com/springfield-general"
            ),
            Clinic(
                id = 2,
                name = "Metropolis Health Center",
                adress = "456 Oak Ave, Metropolis, USA",
                map_location = "https://maps.example.com/metropolis-health"
            ),
            Clinic(
                id = 3,
                name = "Gotham City Clinic",
                adress = "789 Elm St, Gotham, USA",
                map_location = "https://maps.example.com/gotham-clinic"
            )
        )
    }

    suspend fun getAllClinics(): List<Clinic> {
        return simulateDatabaseCall()
    }

    suspend fun getClinicById(id: Int): Clinic? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun createClinic(clinic: Clinic): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        return !simulateDatabaseCall().any { it.id == clinic.id }
    }

    suspend fun updateClinic(clinic: Clinic): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == clinic.id }
    }

    suspend fun deleteClinic(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }

    suspend fun searchClinicsByName(query: String): List<Clinic> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.name.contains(query, ignoreCase = true) }
    }

    suspend fun searchClinicsByAddress(query: String): List<Clinic> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.adress.contains(query, ignoreCase = true) }
    }
}
