package com.example.data.network

data class SubmitFeedbackRequest(
    val patient_id: Int,
    val title: String,
    val description: String
)