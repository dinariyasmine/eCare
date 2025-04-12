package com.example.data.model

data class Medication(
    val id: Int,
    val name: String,
    val dosage: String,
    val frequency: String,
    val instructions: String,
    val prescription_id: Int
)