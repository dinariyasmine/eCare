package com.example.data.model

import java.time.LocalDateTime

data class AvailabilityDto(
    val id: Int,
    val booked: Boolean,
    val doctor_id: Int,
    val start_time: String,
    val end_time: String
)

data class Availability(
    val id: Int,
    val booked: Boolean,
    val doctor_id: Int,
    val start_time: LocalDateTime,
    val end_time: LocalDateTime
)

data class AvailabilityRequest(
    val doctor_id: Int,
    val start_time: String,
    val end_time: String
)