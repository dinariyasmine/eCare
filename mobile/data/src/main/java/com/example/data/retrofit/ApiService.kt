package com.example.data.retrofit

import com.example.data.model.AuthResponse
import com.example.data.model.Clinic
import com.example.data.model.Doctor
import com.example.data.model.GoogleAuthRequest
import com.example.data.model.LoginRequest
import com.example.data.model.MessageResponse
import com.example.data.model.OtpVerificationModel
import com.example.data.model.PasswordResetModel
import com.example.data.model.PasswordResetRequestModel
import com.example.data.model.RegistrationRequest
import com.example.data.model.RegistrationResponse
import com.example.data.model.SocialMedia
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/register/patient/")
    suspend fun registerPatient(@Body request: RegistrationRequest): RegistrationResponse

    @POST("api/auth/register/doctor/")
    suspend fun registerDoctor(@Body request: RegistrationRequest): RegistrationResponse

    @POST("api/auth/login/")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // Password Reset Endpoints
    @POST("api/auth/password-reset/request/")
    suspend fun requestPasswordReset(@Body request: PasswordResetRequestModel): MessageResponse

    @POST("api/auth/password-reset/verify-otp/")
    suspend fun verifyOtp(@Body request: OtpVerificationModel): MessageResponse

    @POST("api/auth/password-reset/reset/")
    suspend fun resetPassword(@Body request: PasswordResetModel): MessageResponse

    // Clinic Endpoints
    // Clinic Endpoints
    @GET("api/clinics/")
    suspend fun getAllClinics(): List<Clinic>

    @GET("api/clinics/{id}/")
    suspend fun getClinicById(@Path("id") id: Int): Clinic

    @POST("api/auth/google-auth/")
    suspend fun googleAuth(@Body googleAuthRequest: GoogleAuthRequest): AuthResponse
    // The same for other methods that return List:
    suspend fun searchClinicsByName(query: String): List<Clinic>
    suspend fun searchClinicsByAddress(query: String): List<Clinic>

    @POST("api/social-media/")
    suspend fun createSocialMedia(@Body socialMedia: SocialMedia): Response<SocialMedia>

    @GET("api/doctors/user/{userId}/")
    suspend fun getDoctorByUserId(@Path("userId") userId: Int): Response<Doctor>

    @GET("api/doctors/{doctorId}/")
    suspend fun getDoctorById(@Path("doctorId") doctorId: Int): Response<Doctor>
}