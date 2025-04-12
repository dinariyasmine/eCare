package com.example.data.repository.inMemory

import android.annotation.SuppressLint
import com.example.data.model.Feedback
import java.text.SimpleDateFormat
import java.util.*

class InMemoryFeedbackRepository {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    @SuppressLint("SimpleDateFormat")
    private val timeSdf = SimpleDateFormat("HH:mm:ss")
    private val feedbacks = mutableListOf<Feedback>()

    init {
        // Initialize with dummy data
        feedbacks.addAll(
            listOf(
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
        )
    }

    fun getAllFeedbacks(): List<Feedback> {
        return feedbacks.toList()
    }

    fun getFeedbackById(id: Int): Feedback? {
        return feedbacks.find { it.id == id }
    }

    fun getFeedbacksByPatientId(patientId: Int): List<Feedback> {
        return feedbacks.filter { it.patient_id == patientId }
    }

    fun getFeedbacksByDoctorId(doctorId: Int): List<Feedback> {
        return feedbacks.filter { it.doctor_id == doctorId }
    }

    fun getFeedbacksByDate(date: Date): List<Feedback> {
        val dateString = sdf.format(date)
        return feedbacks.filter { sdf.format(it.date_creation) == dateString }
    }

    fun createFeedback(feedback: Feedback): Boolean {
        // Check if feedback with the same ID already exists
        if (feedbacks.any { it.id == feedback.id }) {
            return false
        }
        return feedbacks.add(feedback)
    }

    fun updateFeedback(feedback: Feedback): Boolean {
        val index = feedbacks.indexOfFirst { it.id == feedback.id }
        if (index != -1) {
            feedbacks[index] = feedback
            return true
        }
        return false
    }

    fun deleteFeedback(id: Int): Boolean {
        val initialSize = feedbacks.size
        feedbacks.removeIf { it.id == id }
        return feedbacks.size < initialSize
    }

    fun searchFeedbacksByTitle(query: String): List<Feedback> {
        return feedbacks.filter { it.title.contains(query, ignoreCase = true) }
    }

    fun searchFeedbacksByDescription(query: String): List<Feedback> {
        return feedbacks.filter { it.description.contains(query, ignoreCase = true) }
    }
}
