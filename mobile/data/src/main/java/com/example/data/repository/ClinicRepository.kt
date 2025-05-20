package com.example.data.repository

import android.util.Log
import com.example.data.model.Clinic
import com.example.data.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClinicRepository {
    private val TAG = "ClinicRepository"

    suspend fun getAllClinics(): List<Clinic> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching all clinics from API")
            val response = RetrofitInstance.apiService.getAllClinics()
            Log.d(TAG, "Successfully fetched ${response.size} clinics")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching clinics", e)
            emptyList()
        }
    }

    suspend fun getClinicById(id: Int): Clinic? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching clinic with ID: $id")
            val response = RetrofitInstance.apiService.getClinicById(id)
            Log.d(TAG, "Successfully fetched clinic: ${response.name}")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching clinic with ID: $id", e)
            null
        }
    }

    suspend fun searchClinicsByName(query: String): List<Clinic> {
        Log.d(TAG, "Searching clinics by name: $query")
        // Get all clinics and filter by name client-side
        val allClinics = getAllClinics()
        return allClinics.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    suspend fun searchClinicsByAddress(query: String): List<Clinic> {
        Log.d(TAG, "Searching clinics by address: $query")
        // Get all clinics and filter by address client-side
        val allClinics = getAllClinics()
        return allClinics.filter {
            it.adress.contains(query, ignoreCase = true)
        }
    }

    // The following methods are kept for backward compatibility
    // They would need actual API endpoints in a production environment

    suspend fun createClinic(clinic: Clinic): Boolean {
        Log.d(TAG, "createClinic called, but no API endpoint is implemented")
        // This would require a real API endpoint
        return false
    }

    suspend fun updateClinic(clinic: Clinic): Boolean {
        Log.d(TAG, "updateClinic called, but no API endpoint is implemented")
        // This would require a real API endpoint
        return false
    }

    suspend fun deleteClinic(id: Int): Boolean {
        Log.d(TAG, "deleteClinic called, but no API endpoint is implemented")
        // This would require a real API endpoint
        return false
    }
}