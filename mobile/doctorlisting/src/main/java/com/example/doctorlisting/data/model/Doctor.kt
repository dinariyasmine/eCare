package com.example.doctorlisting.data.model

// shared/src/commonMain/kotlin/com/example/shared/model/Doctor.kt


data class Doctor(
    val id: Int,
    val name: String,
    val specialty: String,
    val rating: Double,
    val distance: String,
    val image: String, // URL or resource identifier
    val education: String,
    val experience: String,
    val bio: String,
    val reviews: List<Review>,
    val patients: Int,
    val payment: Double
)


data class Review(
    val author: String,
    val text: String
)