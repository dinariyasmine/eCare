package com.example.data.model

import java.util.Date

data class Prescription(
    val id: Int,
    val patient_id: Int,
    val doctor_id: Int,
    val date: Date
)