package com.example.data.repository

import com.example.data.model.*
import com.example.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val apiService: ApiService) {
    suspend fun registerPatient(request: RegistrationRequest): AuthResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.registerPatient(request.copy(role = "patient"))

            AuthResponse(
                access = response.tokens.access,
                refresh = response.tokens.refresh,
                user = response.user
            )
        }
    }

    suspend fun registerDoctor(request: RegistrationRequest): AuthResponse {
        return withContext(Dispatchers.IO) {
            val response = apiService.registerDoctor(request.copy(role = "doctor"))

            AuthResponse(
                access = response.tokens.access,
                refresh = response.tokens.refresh,
                user = response.user
            )
        }
    }

    suspend fun login(username: String, password: String): AuthResponse {
        return apiService.login(LoginRequest(username, password))
    }

    // Password Reset functions
    suspend fun requestPasswordReset(email: String): MessageResponse {
        return withContext(Dispatchers.IO) {
            apiService.requestPasswordReset(PasswordResetRequestModel(email))
        }
    }

    suspend fun verifyOtp(email: String, otpCode: String): MessageResponse {
        return withContext(Dispatchers.IO) {
            apiService.verifyOtp(OtpVerificationModel(email, otpCode))
        }
    }

    suspend fun resetPassword(email: String, otpCode: String, password: String, password2: String): MessageResponse {
        return withContext(Dispatchers.IO) {
            apiService.resetPassword(PasswordResetModel(email, otpCode, password, password2))
        }
    }
}