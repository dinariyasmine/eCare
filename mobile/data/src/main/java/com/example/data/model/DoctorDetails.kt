package com.example.data.model

data class DoctorDetails(
    val id: Int,
    val user: Any, // Can be either a User object or just an ID
    val photo: String? = null,
    val specialty: String? = null,
    val clinic: Int? = null,
    val grade: Double? = null,
    val description: String? = null,
    val nbr_patients: Int? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)
