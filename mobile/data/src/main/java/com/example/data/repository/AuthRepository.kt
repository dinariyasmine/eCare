package com.example.data.repository

import com.example.data.model.AuthResponse
import com.example.data.model.LoginRequest
import com.example.data.model.RegistrationRequest
import com.example.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val apiService: ApiService) {
    suspend fun registerPatient(request: RegistrationRequest): AuthResponse {
        return withContext(Dispatchers.IO) {
            apiService.registerPatient(
                request.copy(role = "patient")
            )
        }
    }

    suspend fun registerDoctor(request: RegistrationRequest): AuthResponse {
        return withContext(Dispatchers.IO) {
            apiService.registerDoctor(
                request.copy(role = "doctor")
            )
        }
    }
    suspend fun login(username: String, password: String): AuthResponse {
        return apiService.login(LoginRequest(username, password))
    }

}