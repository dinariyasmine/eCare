package com.example.data.model

data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val password2: String,
    val name: String,
    val phone: String,
    val address: String,
    val birth_date: String,
    val role: String? = null,
    val specialty: String? = null,
    val clinic_id: Int? = null
)
