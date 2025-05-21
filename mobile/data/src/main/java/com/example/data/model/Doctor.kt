package com.example.data.model

data class Doctor(
    val id: Int,
    val user_id: Int,
    val photo: String,
    val specialty: String,
    val clinic_id: Int,
    val grade: Float,
    val description: String,
    val nbr_patients: Int
)