package com.example.data.network

import com.example.data.model.Doctor
import com.example.data.model.Notification
import com.example.data.model.Patient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
    suspend fun updateDoctorById(
        @Path("id") doctorId: Int,
        @Body updatedData: UpdateDoctorRequest
    ): Response<Doctor>

    @POST("/api/submit-feedback/{doctor_id}/")
    suspend fun submitFeedback(
        @Path("doctor_id") doctorId: Int,
        @Body feedback: SubmitFeedbackRequest
    ): Response<ResponseMessage>

    @POST("api/devices/register/")
    suspend fun registerDevice(
        @retrofit2.http.Body deviceInfo: DeviceRegistration
    ): Response<DeviceRegistrationResponse>
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
