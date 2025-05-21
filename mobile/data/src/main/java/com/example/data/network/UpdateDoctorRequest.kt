package com.example.data.network


data class UpdateDoctorRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val birth_date: String? = null,
    val specialty: String? = null,
    val clinic: String? = null,
    val grade: Double? = null,
    val description: String? = null,
    val nbr_patients: Int? = null,
    val clinic_pos: String? = null
)
