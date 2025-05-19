package com.example.data.network

import com.example.data.model.Doctor

// DoctorResponse.kt
data class DoctorResponse(
    val doctors: List<Doctor>,
    val doctor : Doctor
)