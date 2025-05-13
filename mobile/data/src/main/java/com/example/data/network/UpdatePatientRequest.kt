package com.example.data.network

data class UpdatePatientRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val birth_date: String
)
