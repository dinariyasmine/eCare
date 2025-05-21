package com.example.data.model

data class Medication(
    val id: Int,
    val name: String,
    val description: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)
