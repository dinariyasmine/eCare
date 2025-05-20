package com.example.data.retrofit

import com.example.data.model.AuthResponse
import com.example.data.model.LoginRequest
import com.example.data.model.MessageResponse
import com.example.data.model.OtpVerificationModel
import com.example.data.model.PasswordResetModel
import com.example.data.model.PasswordResetRequestModel
import com.example.data.model.RegistrationRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/register/patient/")
    suspend fun registerPatient(@Body request: RegistrationRequest): AuthResponse

    @POST("api/auth/register/doctor/")
    suspend fun registerDoctor(@Body request: RegistrationRequest): AuthResponse

    @POST("api/auth/login/")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // Password Reset Endpoints
    @POST("api/auth/password-reset/request/")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequestModel): MessageResponse

    @POST("api/auth/password-reset/verify-otp/")
    suspend fun verifyOtp(@Body request: OtpVerificationModel): MessageResponse

    @POST("api/auth/password-reset/reset/")
    suspend fun resetPassword(@Body request: PasswordResetModel): MessageResponse


}