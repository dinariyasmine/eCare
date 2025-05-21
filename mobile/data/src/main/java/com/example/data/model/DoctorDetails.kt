package com.example.data.model

data class DoctorDetails(
    val doctor: Doctor,
    val user: User,
    val clinic: Clinic?,
    val feedbacks: List<Feedback>?

)
