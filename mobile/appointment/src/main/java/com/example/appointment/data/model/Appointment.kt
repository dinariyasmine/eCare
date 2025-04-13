package com.example.appointment.data.model

import java.time.LocalDate
import java.time.LocalTime

data class Appointment(
    val id: Int,
    val doctor: Doctor,
    val patient: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val status: Status,
    val qrCode: String
)