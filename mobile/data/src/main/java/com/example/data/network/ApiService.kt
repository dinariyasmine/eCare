package com.example.data.network

import com.example.data.model.AuthResponse
import com.example.data.model.Doctor
import com.example.data.model.LoginRequest
import com.example.data.model.Medication
import com.example.data.model.MessageResponse
import com.example.data.model.Notification
import com.example.data.model.OtpVerificationModel
import com.example.data.model.PasswordResetModel
import com.example.data.model.PasswordResetRequestModel
import com.example.data.model.Patient
import com.example.data.model.Prescription
import com.example.data.model.RegistrationRequest
import com.example.data.model.RegistrationResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {
    @GET("api/notifications/")
    suspend fun getNotifications(): Response<List<Notification>>

    @GET("api/notifications/unread/")
    suspend fun getUnreadNotifications(): Response<List<Notification>>

    @GET("/api/doctors/")
    suspend fun getDoctors(): DoctorResponse
    @GET("api/doctor/{id}/")
    suspend fun getDoctorDetailsById(@Path("id") doctorId: Int): DoctorResponse
    @GET("api/get-doctor-feedback/{doctor_id}/")
    suspend fun getDoctorFeedbackById(@Path("doctor_id") doctorId: Int): List<FeedbackResponse>
    @GET("patients")
    suspend fun getPatients(): PatientListResponse

    @POST("api/notifications/{id}/mark_as_read/")
    suspend fun markNotificationAsRead(@Path("id") notificationId: Int): Response<Map<String, String>>
    @GET("/api/patients/{id}")
    suspend fun getPatientById(@Path("id") id: Int): Patient
    @PUT("/api/patients/{id}/update/")
    suspend fun updatePatientById(
        @Path("id") id: Int,
        @Body updatedData: Map<String, Any?>
    ): ResponseMessage

    @POST("api/notifications/mark_all_as_read/")
    suspend fun markAllNotificationsAsRead(): Response<Map<String, String>>
    @PUT("api/patients/{id}/update/")
    suspend fun updatePatient(
        @Path("id") id: Int,
        @Body updatedFields: UpdatePatientRequest
    ): Response<Patient>

    @PUT("/api/doctors/{id}/update/")
    suspend fun updateDoctorById(@Path("id") doctorId: Int, @Body updatedFields: UpdateDoctorRequest): retrofit2.Response<Any>

    @POST("/api/submit-feedback/{doctor_id}/")
    suspend fun submitFeedback(
        @Path("doctor_id") doctorId: Int,
        @Body feedback: SubmitFeedbackRequest
    ): Response<ResponseMessage>

    @POST("api/devices/register/")
    suspend fun registerDevice(
        @retrofit2.http.Body deviceInfo: DeviceRegistration
    ): Response<DeviceRegistrationResponse>



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


    @GET("api/prescriptions/")
    suspend fun getPrescriptions(): List<Prescription>

    @GET("api/prescriptions/{id}/")
    suspend fun getPrescriptionById(@Path("id") id: Int): Prescription

    @POST("api/prescriptions/")
    suspend fun createPrescription(@Body data: Map<String, @JvmSuppressWildcards Any>): Prescription



    @GET("api/prescriptions/{id}/generate_pdf/")
    @Streaming
    suspend fun generatePrescriptionPdf(@Path("id") prescriptionId: Int): ResponseBody

    @GET("api/medications/")
    suspend fun getMedications(): List<Medication>

    @POST("api/prescriptions/{id}/add_medication/")
    suspend fun addMedicationToPrescription(
        @Path("id") prescriptionId: Int,
        @Body data: Map<String, @JvmSuppressWildcards Any>
    ): Any

    @GET("api/medications/{id}/")
    suspend fun getMedicationById(@Path("id") id: Int): Medication

    @POST("api/medications/")
    suspend fun createMedication(@Body data: Map<String, Any>): Medication

}



data class DeviceRegistration(
    val registration_id: String,
    val type: String = "android",
    val name: String = "Android Device"
)

data class DeviceRegistrationResponse(
    val success: Boolean,
    val created: Boolean
)
