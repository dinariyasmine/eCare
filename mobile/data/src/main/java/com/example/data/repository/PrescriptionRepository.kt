package com.example.data.repository

import android.util.Log
import com.example.data.model.Prescription
import com.example.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrescriptionRepository(private val apiService: ApiService) {

    suspend fun getPrescriptions(): List<Prescription> = withContext(Dispatchers.IO) {
        try {
            Log.d("PrescriptionRepository", "Fetching all prescriptions")
            val prescriptions = apiService.getPrescriptions()
            Log.d("PrescriptionRepository", "Fetched ${prescriptions.size} prescriptions")
            prescriptions
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error fetching prescriptions: ${e.message}", e)
            throw e
        }
    }

    suspend fun getPrescriptionById(id: Int): Prescription = withContext(Dispatchers.IO) {
        try {
            Log.d("PrescriptionRepository", "Fetching prescription with ID: $id")
            val prescription = apiService.getPrescriptionById(id)
            Log.d("PrescriptionRepository", "Prescription fetched successfully")
            prescription
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error fetching prescription: ${e.message}", e)
            throw e
        }
    }

    suspend fun createPrescription(patientId: Int,doctorId: Int, date: String, notes: String): Int = withContext(Dispatchers.IO) {
        try {
            Log.d("PrescriptionRepository", "Creating prescription with data: patient=$patientId, date=$date")
            val data = mapOf(
                "patient" to patientId,
                "doctor" to doctorId,
                "date" to date,
                "notes" to notes
            )
            val prescription = apiService.createPrescription(data)
            Log.d("PrescriptionRepository", "Prescription created with ID: ${prescription.id}")
            prescription.id
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error creating prescription: ${e.message}", e)
            throw e
        }
    }

    suspend fun addMedicationToPrescription(
        prescriptionId: Int,
        medicationId: Int,
        dosage: String,
        duration: String,
        frequency: String,
        instructions: String
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d("PrescriptionRepository", "Adding medication $medicationId to prescription $prescriptionId")
            val data = mapOf(
                "medication_id" to medicationId,  // CORRECT: Changed from "medication" to "medication_id"
                "dosage" to dosage,
                "duration" to duration,
                "frequency" to frequency,
                "instructions" to instructions
            )

            // Add debug logging to see what you're sending
            Log.d("PrescriptionRepository", "Request data: $data")

            val response = apiService.addMedicationToPrescription(prescriptionId, data)
            Log.d("PrescriptionRepository", "Medication added successfully")
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error adding medication: ${e.message}", e)
            throw e
        }
    }


    suspend fun generatePrescriptionPdf(prescriptionId: Int): String? = withContext(Dispatchers.IO) {
        try {
            Log.d("PrescriptionRepository", "Generating PDF for prescription $prescriptionId")
            val responseBody = apiService.generatePrescriptionPdf(prescriptionId)

            // Save the PDF to a file or return the URL from another endpoint
            val pdfUrl = "http://ea18-105-102-48-10.ngrok-free.app/prescriptions/prescription_$prescriptionId.pdf"
            Log.d("PrescriptionRepository", "PDF generated: $pdfUrl")
            pdfUrl
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error generating PDF: ${e.message}", e)
            null
        }
    }

}
