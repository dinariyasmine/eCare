package com.example.data.repository.inMemory

import com.example.data.model.Clinic

class InMemoryClinicRepository {
    private val clinics = mutableListOf<Clinic>()

    init {
        // Initialize with dummy data
        clinics.addAll(
            listOf(
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
        )
    }

    fun getAllClinics(): List<Clinic> {
        return clinics.toList()
    }

    fun getClinicById(id: Int): Clinic? {
        return clinics.find { it.id == id }
    }

    fun createClinic(clinic: Clinic): Boolean {
        // Check if clinic with the same ID already exists
        if (clinics.any { it.id == clinic.id }) {
            return false
        }
        return clinics.add(clinic)
    }

    fun updateClinic(clinic: Clinic): Boolean {
        val index = clinics.indexOfFirst { it.id == clinic.id }
        if (index != -1) {
            clinics[index] = clinic
            return true
        }
        return false
    }

    fun deleteClinic(id: Int): Boolean {
        val initialSize = clinics.size
        clinics.removeIf { it.id == id }
        return clinics.size < initialSize
    }

    fun searchClinicsByName(query: String): List<Clinic> {
        return clinics.filter { it.name.contains(query, ignoreCase = true) }
    }

    fun searchClinicsByAddress(query: String): List<Clinic> {
        return clinics.filter { it.adress.contains(query, ignoreCase = true) }
    }
}
