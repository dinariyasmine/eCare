package com.example.data.repository

import android.util.Log
import com.example.data.model.Doctor
import com.example.data.network.ApiClient
import com.example.data.network.UpdateDoctorRequest
import com.example.data.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//class DoctorRepository {
//
//    // Simulate data fetching
//    fun getDoctorDetailsById(doctorId: Int): DoctorDetails? {
//        // Simulate getting a doctor
//        val doctor = listOf(
//            Doctor(1, 101, "url_to_photo_1", "Cardiology", 201, 4.5f, "Experienced cardiologist", 120),
//            Doctor(2, 102, "url_to_photo_2", "Dermatology", 202, 4.2f, "Expert in skin care", 98)
//        ).find { it.id == doctorId } ?: return null
//
//        // Simulate fetching the associated user
//        val user = listOf(
//            User(101, "Dr. Smith", "smith@email.com", "pass123", "555-1234", "123 Main St", Role.DOCTOR, Date()),
//            User(102, "Dr. Jane", "jane@email.com", "pass456", "555-5678", "456 Elm St", Role.DOCTOR, Date())
//        ).find { it.id == doctor.user_id } ?: return null
//        val now = Date()
//        val feedback = listOf(
//    Feedback(
//        id = 3,
//        title = "Satisfactory Visit",
//        description = "The consultation was good, but the wait time was long.",
//        patient_id = 103,
//        doctor_id = 203,
//        date_creation = now,
//        time_creation = now
//    ),
//            Feedback(
//                id = 3,
//                title = "Satisfactory Visit",
//                description = "The consultation was good, but the wait time was long.",
//                patient_id = 103,
//                doctor_id = 203,
//                date_creation = now,
//                time_creation = now
//            ),
//            Feedback(
//                id = 3,
//                title = "Satisfactory Visit",
//                description = "The consultation was good, but the wait time was long.",
//                patient_id = 103,
//                doctor_id = 203,
//                date_creation = now,
//                time_creation = now
//            )
//)
//
//        return DoctorDetails(
//            doctor, user, clinic = null , feedbacks = feedback
//        )
//    }
//
//    fun getAllDoctors(): List<Doctor> {
//        // Simulate a list of doctors
//        return listOf(
//            Doctor(1, 101, "url_to_photo_1", "Cardiology", 201, 4.5f, "Experienced cardiologist", 120),
//            Doctor(2, 102, "url_to_photo_2", "Dermatology", 202, 4.2f, "Expert in skin care", 98),
//            Doctor(3, 103, "url_to_photo_3", "Neurology", 203, 4.7f, "Specialized in brain surgeries", 150)
//        )
//    }
//
//

    class DoctorRepository {
        suspend fun getDoctorByUserId(userId: Int): Doctor? = withContext(Dispatchers.IO) {
            try {
                Log.d("DoctorRepository", "Getting doctor by user ID: $userId")

                val response = RetrofitInstance.apiService.getDoctorByUserId(userId)

                // Log the response
                Log.d("API_RESPONSE", "Response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val doctor = response.body()
                    Log.d("DoctorRepository", "Found doctor: $doctor")
                    return@withContext doctor
                } else {
                    Log.e("DoctorRepository", "Failed to get doctor: ${response.errorBody()?.string()}")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("DoctorRepository", "Error getting doctor by user ID", e)
                return@withContext null
            }
        }
        suspend fun getAllDoctors(): List<Doctor> {
            val response =ApiClient.apiService.getDoctors()
            val doctors = response.doctors
            return doctors // Calls the API to fetch all doctors
        }
        suspend fun updateDoctor(doctorId: Int, updatedFields: UpdateDoctorRequest): String {
            Log.d("DoctorRepository", "Updating doctor with ID: $doctorId, updatedFields: $updatedFields")

            val response = ApiClient.apiService.updateDoctorById(doctorId, updatedFields)
            if (response.isSuccessful) {
                return "Update successful"
            } else {
                Log.e("DoctorRepository", "Update failed: ${response.message()}")
                throw Exception("Update failed: ${response.message()}")
            }
        }
        suspend fun getDoctorDetailsById(doctorId: Int): Doctor {
            // Make an API call to get the doctor details by ID
            val response = ApiClient.apiService.getDoctorDetailsById(doctorId)

            // Assuming the response contains a "doctor" field with the doctor's details
            val doctorData = response.doctor // 'doctor' instead of 'doctors' based on your response format

            // Return the Doctor object from the response
            return doctorData
        }

    }


