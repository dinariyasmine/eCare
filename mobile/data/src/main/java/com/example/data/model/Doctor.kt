package com.example.data.model

// Doctor.kt (Model)
data class Doctor(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val role: String,
    val birth_date: String,
    val specialty: String,
    val clinic: String,
    val grade: Double,
    val description: String,
    val nbr_patients: Int,
    val clinic_pos :String,
)
