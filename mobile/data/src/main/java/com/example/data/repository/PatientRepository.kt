package com.example.data.repository

import android.util.Log
import com.example.data.model.Patient
import com.example.data.network.ApiClient
import com.example.data.network.UpdatePatientRequest
import com.example.data.retrofit.ApiService

class PatientRepository(apiService: ApiService) {

    // Fetch all patients from the API
    suspend fun getAllPatients(): List<Patient> {
        val response = ApiClient.apiService.getPatients()
        return response.patients
    }

    // Fetch a single patient by ID
    suspend fun getPatientById(patientId: Int): Patient {
        val response = ApiClient.apiService.getPatientById(patientId)
        return response
    }


    // Update a patient's info
    suspend fun updatePatient(patientId: Int, updatedFields: UpdatePatientRequest): String {
        Log.d("ntiiiiiiiRepository", "Updating patient with ID: $patientId, updatedFields: $updatedFields")

        val response = ApiClient.apiService.updatePatient(patientId, updatedFields)
        if (response.isSuccessful) {
            return "Update successful"
        } else {
            Log.e("PatientRepositoryfaill", "Update failed: ${response.message()}")
            throw Exception("Update failed: ${response.message()}")
        }
    }
}


