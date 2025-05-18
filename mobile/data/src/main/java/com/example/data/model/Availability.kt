package com.example.data.model

import java.util.Date

data class Availability(
    val id: Int,
    val booked: Boolean=false,
    val doctor_id: Int,
    val start_time: Date,
    val end_time: Date
)

data class AvailabilityRequest(
    val booked: Boolean=false,
    val doctor_id: Int,
    val start_time: Date,
    val end_time: Date
)