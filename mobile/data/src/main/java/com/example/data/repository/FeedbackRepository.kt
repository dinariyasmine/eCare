package com.example.data.repository

import com.example.data.model.Feedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FeedbackRepository {
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val timeSdf = SimpleDateFormat("HH:mm:ss")

    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Feedback> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        return@withContext listOf(
            Feedback(
                id = 1,
                title = "Great experience",
                description = "Dr. Smith was very thorough and took the time to explain everything clearly.",
                patient_id = 201,
                doctor_id = 1,
                date_creation = sdf.parse("2025-04-10") as Date,
                time_creation = timeSdf.parse("14:30:00") as Date
            ),
            Feedback(
                id = 2,
                title = "Excellent care",
                description = "The doctor was very attentive and the staff was friendly. Highly recommend!",
                patient_id = 202,
                doctor_id = 2,
                date_creation = sdf.parse("2025-04-11") as Date,
                time_creation = timeSdf.parse("10:15:00") as Date
            ),
            Feedback(
                id = 3,
                title = "Quick and efficient",
                description = "My appointment was on time and the doctor addressed all my concerns efficiently.",
                patient_id = 203,
                doctor_id = 1,
                date_creation = sdf.parse("2025-04-12") as Date,
                time_creation = timeSdf.parse("16:45:00") as Date
            )
        )
    }

    suspend fun getAllFeedbacks(): List<Feedback> {
        return simulateDatabaseCall()
    }

    suspend fun getFeedbackById(id: Int): Feedback? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun getFeedbacksByPatientId(patientId: Int): List<Feedback> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.patient_id == patientId }
    }

    suspend fun getFeedbacksByDoctorId(doctorId: Int): List<Feedback> {
//        delay(500) // Simulate delay
//        return simulateDatabaseCall().filter { it.doctor_id == doctorId }
        val now = Date()
        return listOf(
            Feedback(
                id = 1,
                title = "Great Experience",
                description = "The doctor was very helpful and kind.",
                patient_id = 101,
                doctor_id = 201,
                date_creation = now,
                time_creation = now
            ),
            Feedback(
                id = 2,
                title = "Quick Diagnosis",
                description = "Got my diagnosis quickly and efficiently.",
                patient_id = 102,
                doctor_id = 202,
                date_creation = now,
                time_creation = now
            ),
            Feedback(
                id = 3,
                title = "Satisfactory Visit",
                description = "The consultation was good, but the wait time was long.",
                patient_id = 103,
                doctor_id = 203,
                date_creation = now,
                time_creation = now
            ))
    }

    suspend fun getFeedbacksByDate(date: Date): List<Feedback> {
        delay(500) // Simulate delay
        val dateString = sdf.format(date)
        return simulateDatabaseCall().filter { sdf.format(it.date_creation) == dateString }
    }

    suspend fun createFeedback(feedback: Feedback): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        return !simulateDatabaseCall().any { it.id == feedback.id }
    }

    suspend fun updateFeedback(feedback: Feedback): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == feedback.id }
    }

    suspend fun deleteFeedback(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }

    suspend fun searchFeedbacksByTitle(query: String): List<Feedback> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.title.contains(query, ignoreCase = true) }
    }

    suspend fun searchFeedbacksByDescription(query: String): List<Feedback> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.description.contains(query, ignoreCase = true) }
    }
}
