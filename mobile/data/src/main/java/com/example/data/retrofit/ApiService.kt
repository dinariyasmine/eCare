package com.example.data.retrofit

import com.example.data.model.AuthResponse
import com.example.data.model.RegistrationRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/register/patient/")
    suspend fun registerPatient(@Body request: RegistrationRequest): AuthResponse

    @POST("api/auth/register/doctor/")
    suspend fun registerDoctor(@Body request: RegistrationRequest): AuthResponse
}