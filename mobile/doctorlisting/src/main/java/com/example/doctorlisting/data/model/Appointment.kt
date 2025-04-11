package com.example.doctorlisting.data.model

data class Appointment(
    val time: String,
    val doctorName: String,
    val status: String // e.g. "In Progress", "Completed"
)