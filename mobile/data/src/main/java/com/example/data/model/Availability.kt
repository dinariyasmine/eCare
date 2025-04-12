package com.example.data.model

import java.util.Date

data class Availability(
    val id: Int,
    val doctor_id: Int,
    val start_time: Date,
    val end_time: Date
)