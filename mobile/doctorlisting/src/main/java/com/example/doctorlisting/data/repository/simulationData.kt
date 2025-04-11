package com.example.doctorlisting.data.repository

// shared/src/commonMain/kotlin/com/example/shared/data/DoctorDataSource.kt

import com.example.doctorlisting.data.model.Doctor
import com.example.doctorlisting.data.model.Review

object DoctorDataSource {
    private val doctors: List<Doctor> = listOf(
        Doctor(
            id = 1,
            name = "Dr. Rachid",
            specialty = "Cardiologist",
            rating = 4.6,
            distance = "2.4km away",
            image = "res/drawable/doctorhome.png",
            education = "MBBS, MD (Cardiology)",
            experience = "10+ Years",
            bio = "Dr. Rachid is a highly experienced cardiologist.",
            patients = 150,
            payment = 100.0,
            reviews = listOf(
                Review(author = "John Doe", text = "Great doctor!"),
                Review(author = "Jane Smith", text = "Very professional.")
            )
        ),
        Doctor(
            id = 2,
            name = "Dr. Jane",
            specialty = "Cardiologist",
            rating = 4.8,
            distance = "1.8km away",
            image = "doctor2.jpg",
            education = "MBBS, MD (Cardiology)",
            experience = "8+ Years",
            bio = "Dr. Jane is a board-certified cardiologist.",
            patients = 120,
            payment = 90.0,
            reviews = listOf(
                Review(author = "Alice Brown", text = "Excellent care!"),
                Review(author = "Bob Green", text = "Highly recommended.")
            )
        )
        // Add more doctors here
    )

    fun getDoctors(): List<Doctor> {
        return doctors
    }

    fun getDoctorById(id: Int): Doctor? {
        return doctors.find { it.id == id }
    }
}