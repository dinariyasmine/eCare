package com.example.data.repository

import com.example.data.model.Feedback
import com.example.data.network.ApiClient.apiService
import com.example.data.network.FeedbackResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import com.example.data.network.SubmitFeedbackRequest

class FeedbackRepository {
    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val timeSdf = SimpleDateFormat("HH:mm:ss")

    // Simulating a database call with a delay
    suspend fun submitFeedback(doctorId: Int, feedbackRequest: SubmitFeedbackRequest): Boolean {
        return try {
            val response = apiService.submitFeedback(doctorId, feedbackRequest)
            if (response.isSuccessful) {
                Log.d("FeedbackRepository", "Feedback submitted successfully: ${response.body()?.message}")
                true
            } else {
                Log.e("FeedbackRepository", "Failed to submit feedback: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("FeedbackRepository", "Exception during feedback submission: ${e.message}", e)
            false
        }
    }


//    suspend fun getAllFeedbacks(): List<Feedback> {
//        return simulateDatabaseCall()
//    }

//    suspend fun getFeedbackById(id: Int): Feedback? {
//        delay(500) // Simulate delay
//        return simulateDatabaseCall().find { it.id == id }
//    }

//    suspend fun getFeedbacksByPatientId(patientId: Int): List<Feedback> {
//        delay(500) // Simulate delay
//        return simulateDatabaseCall().filter { it.patient_id == patientId }
//    }

    suspend fun getFeedbacksByDoctorId(doctorId: Int): List<Feedback> {
        try {
            Log.d("FeedbackRepository", "Fetching feedbacks for doctor $doctorId")
            // Make the API call to fetch feedback for the given doctor
            val feedbackResponses: List<FeedbackResponse> = apiService.getDoctorFeedbackById(doctorId)
            Log.d("FeedbackRepository", "Received ${feedbackResponses.size} feedback responses")
            
            // Log each response for debugging
            feedbackResponses.forEach { response ->
                Log.d("FeedbackRepository", "Response: id=${response.id}, title=${response.title}, " +
                    "description=${response.description}, patient_name=${response.patient_name}, " +
                    "date=${response.date_creation}, time=${response.time_creation}")
            }

            // Convert FeedbackResponse to Feedback model
            return feedbackResponses.map { feedbackResponse ->
                try {
                    Feedback(
                        id = feedbackResponse.id,
                        title = feedbackResponse.title,
                        description = feedbackResponse.description,
                        patient_id = feedbackResponse.patient_name ?: "Anonymous Patient", // Provide default value if patient_name is null
                        doctor_id = doctorId,
                        date_creation = sdf.parse(feedbackResponse.date_creation) ?: Date(),
                        time_creation = timeSdf.parse(feedbackResponse.time_creation) ?: Date()
                    )
                } catch (e: Exception) {
                    Log.e("FeedbackRepository", "Error converting feedback response: ${e.message}", e)
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e("FeedbackRepository", "Error fetching feedbacks: ${e.message}", e)
            throw Exception("Error fetching feedback: ${e.message}")
        }
    }


//    suspend fun getFeedbacksByDate(date: Date): List<Feedback> {
//        delay(500) // Simulate delay
//        val dateString = sdf.format(date)
//        return simulateDatabaseCall().filter { sdf.format(it.date_creation) == dateString }
//    }

//    suspend fun createFeedback(feedback: Feedback): Boolean {
//        delay(500) // Simulate delay
//        // In a real implementation, this would insert into a database
//        // For now, we'll just simulate success or failure
//        return !simulateDatabaseCall().any { it.id == feedback.id }
//    }

//    suspend fun updateFeedback(feedback: Feedback): Boolean {
//        delay(500) // Simulate delay
//        // In a real implementation, this would update the database
//        // For now, we'll just simulate success or failure
//        return simulateDatabaseCall().any { it.id == feedback.id }
//    }

//    suspend fun deleteFeedback(id: Int): Boolean {
//        delay(500) // Simulate delay
//        // In a real implementation, this would delete from the database
//        // For now, we'll just simulate success or failure
//        return simulateDatabaseCall().any { it.id == id }
//    }
//
//    suspend fun searchFeedbacksByTitle(query: String): List<Feedback> {
//        delay(500) // Simulate delay
//        return simulateDatabaseCall().filter { it.title.contains(query, ignoreCase = true) }
//    }
//
//    suspend fun searchFeedbacksByDescription(query: String): List<Feedback> {
//        delay(500) // Simulate delay
//        return simulateDatabaseCall().filter { it.description.contains(query, ignoreCase = true) }
//    }
}
