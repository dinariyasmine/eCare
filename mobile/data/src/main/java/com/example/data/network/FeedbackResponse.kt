package com.example.data.network

data class FeedbackResponse(
    val id: Int,
    val title: String,
    val description: String,
    val date_creation: String,
    val time_creation: String,
    val patient_name: String
)