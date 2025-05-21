package com.example.data.repository

import android.util.Log
import com.example.data.model.Doctor
import com.example.data.network.ApiClient
import com.example.data.network.UpdateDoctorRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DoctorRepository(private val apiService: com.example.data.network.ApiService) {

    suspend fun getAllDoctors(): List<Doctor> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDoctors()
                response.doctors
            } catch (e: Exception) {
                Log.e("DoctorRepository", "Error fetching doctors: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun updateDoctor(doctorId: Int, updatedFields: UpdateDoctorRequest): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("DoctorRepository", "Updating doctor with ID: $doctorId, updatedFields: $updatedFields")
                val response = apiService.updateDoctorById(doctorId, updatedFields)
                if (response.isSuccessful) {
                    "Update successful"
                } else {
                    val errorMsg = "Update failed: ${response.message()}"
                    Log.e("DoctorRepository", errorMsg)
                    throw Exception(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("DoctorRepository", "Error updating doctor: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun getDoctorDetailsById(doctorId: Int): Doctor {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDoctorDetailsById(doctorId)
                response.doctor
            } catch (e: Exception) {
                Log.e("DoctorRepository", "Error fetching doctor details: ${e.message}", e)
                throw e
            }
        }
    }
}
