package com.example.data.model

import java.util.Date

data class Feedback(
    val id: Int,
    val title: String,
    val description: String,
    val patient_id: String,
    val doctor_id: Int, // Changed from String to Int for consistency
    val date_creation: Date,
    val time_creation: Date
)