package com.example.data.repository.inMemory

import android.annotation.SuppressLint
import com.example.data.model.Prescription
import java.text.SimpleDateFormat
import java.util.*

class InMemoryPrescriptionRepository {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val prescriptions = mutableListOf<Prescription>()

    init {
        // Initialize with dummy data
        prescriptions.addAll(
            listOf(
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
        )
    }

    fun getAllPrescriptions(): List<Prescription> {
        return prescriptions.toList()
    }

    fun getPrescriptionById(id: Int): Prescription? {
        return prescriptions.find { it.id == id }
    }

    fun getPrescriptionsByPatientId(patientId: Int): List<Prescription> {
        return prescriptions.filter { it.patient_id == patientId }
    }

    fun getPrescriptionsByDoctorId(doctorId: Int): List<Prescription> {
        return prescriptions.filter { it.doctor_id == doctorId }
    }

    fun getPrescriptionsByDate(date: Date): List<Prescription> {
        val dateString = sdf.format(date)
        return prescriptions.filter { sdf.format(it.date) == dateString }
    }

    fun createPrescription(prescription: Prescription): Boolean {
        // Check if prescription with the same ID already exists
        if (prescriptions.any { it.id == prescription.id }) {
            return false
        }
        return prescriptions.add(prescription)
    }

    fun updatePrescription(prescription: Prescription): Boolean {
        val index = prescriptions.indexOfFirst { it.id == prescription.id }
        if (index != -1) {
            prescriptions[index] = prescription
            return true
        }
        return false
    }

    fun deletePrescription(id: Int): Boolean {
        val initialSize = prescriptions.size
        prescriptions.removeIf { it.id == id }
        return prescriptions.size < initialSize
    }
}
