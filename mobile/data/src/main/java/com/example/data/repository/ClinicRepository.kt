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
    suspend fun getClinic(): Clinic{
        return  Clinic(
            id = 3,
            name = "Gotham City Clinic",
            adress = "789 Elm St, Gotham, USA",
            map_location = "https://www.google.fr/maps/place/Ecole+Nationale+Sup%C3%A9rieure+d'Informatique+(Ex.+INI)/@36.7050457,3.1554615,15z/data=!3m1!4b1!4m6!3m5!1s0x128e522f3f317461:0x215c157a5406653c!8m2!3d36.7050299!4d3.1739156!16s%2Fg%2F120hhrrs?hl=fr&entry=ttu&g_ep=EgoyMDI1MDQwOS4wIKXMDSoJLDEwMjExNDU1SAFQAw%3D%3D"

        )
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
