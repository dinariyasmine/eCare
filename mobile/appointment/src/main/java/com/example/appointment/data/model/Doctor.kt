package com.example.appointment.data.model

data class Doctor(
    val id: Int,
    val name: String,
    val specialty: String,
    val photoUrl: String // or Int for local resources
)