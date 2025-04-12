package com.example.data.repository.inMemory

import com.example.data.model.Medication

class InMemoryMedicationRepository {
    private val medications = mutableListOf<Medication>()

    init {
        // Initialize with dummy data
        medications.addAll(
            listOf(
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
        )
    }

    fun getAllMedications(): List<Medication> {
        return medications.toList()
    }

    fun getMedicationById(id: Int): Medication? {
        return medications.find { it.id == id }
    }

    fun getMedicationsByPrescriptionId(prescriptionId: Int): List<Medication> {
        return medications.filter { it.prescription_id == prescriptionId }
    }

    fun createMedication(medication: Medication): Boolean {
        // Check if medication with the same ID already exists
        if (medications.any { it.id == medication.id }) {
            return false
        }
        return medications.add(medication)
    }

    fun updateMedication(medication: Medication): Boolean {
        val index = medications.indexOfFirst { it.id == medication.id }
        if (index != -1) {
            medications[index] = medication
            return true
        }
        return false
    }

    fun deleteMedication(id: Int): Boolean {
        val initialSize = medications.size
        medications.removeIf { it.id == id }
        return medications.size < initialSize
    }

    fun searchMedicationsByName(query: String): List<Medication> {
        return medications.filter { it.name.contains(query, ignoreCase = true) }
    }
}
