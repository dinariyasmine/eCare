package com.example.data.model

data class Prescription(
    val id: Int,
    val patient: Int,
    val patient_details: PatientDetails? = null,
    val doctor: Int,
    val doctor_details: DoctorDetails? = null,
    val date: String,
    val notes: String? = null,
    val items: List<PrescriptionItem> = emptyList(),
    val pdf_file: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

data class PatientDetails(
    val id: Int,
    val user: Any, // Can be either a User object or just an ID
    val created_at: String? = null,
    val updated_at: String? = null
)



data class PrescriptionItem(
    val id: Int,
    val prescription: Int,
    val medication: Medication,
    val dosage: String,
    val duration: String,
    val frequency: String,
    val instructions: String
)


