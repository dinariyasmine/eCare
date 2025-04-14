package com.example.data.repository.inMemory

import com.example.data.model.Patient

class InMemoryPatientRepository {
    private val patients = mutableListOf<Patient>()

    init {
        // Initialize with dummy data
        patients.addAll(
            listOf(
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
        )
    }

    fun getAllPatients(): List<Patient> {
        return patients.toList()
    }

    fun getPatientById(id: Int): Patient? {
        return patients.find { it.id == id }
    }

    fun getPatientByUserId(userId: Int): Patient? {
        return patients.find { it.user_id == userId }
    }

    fun createPatient(patient: Patient): Boolean {
        // Check if patient with the same ID or user_id already exists
        if (patients.any { it.id == patient.id || it.user_id == patient.user_id }) {
            return false
        }
        return patients.add(patient)
    }

    fun updatePatient(patient: Patient): Boolean {
        val index = patients.indexOfFirst { it.id == patient.id }
        if (index != -1) {
            patients[index] = patient
            return true
        }
        return false
    }

    fun deletePatient(id: Int): Boolean {
        val initialSize = patients.size
        patients.removeIf { it.id == id }
        return patients.size < initialSize
    }
}
