package com.example.data.repository

import android.annotation.SuppressLint
import com.example.data.model.Prescription
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class PrescriptionRepository {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Prescription> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        return@withContext listOf(
            Prescription(
                id = 1,
                patient_id = 1,
                doctor_id = 101,
                date = sdf.parse("2025-04-10") as Date
            ),
            Prescription(
                id = 2,
                patient_id = 2,
                doctor_id = 102,
                date = sdf.parse("2025-04-11") as Date
            ),
            Prescription(
                id = 3,
                patient_id = 1,
                doctor_id = 103,
                date = sdf.parse("2025-04-12") as Date
            )
        )
    }

    suspend fun getAllPrescriptions(): List<Prescription> {
        return simulateDatabaseCall()
    }

    suspend fun getPrescriptionById(id: Int): Prescription? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun getPrescriptionsByPatientId(patientId: Int): List<Prescription> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.patient_id == patientId }
    }

    suspend fun getPrescriptionsByDoctorId(doctorId: Int): List<Prescription> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.doctor_id == doctorId }
    }

    suspend fun getPrescriptionsByDate(date: Date): List<Prescription> {
        delay(500) // Simulate delay
        val dateString = sdf.format(date)
        return simulateDatabaseCall().filter { sdf.format(it.date) == dateString }
    }

    suspend fun createPrescription(prescription: Prescription): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        return !simulateDatabaseCall().any { it.id == prescription.id }
    }

    suspend fun updatePrescription(prescription: Prescription): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == prescription.id }
    }

    suspend fun deletePrescription(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }
}
