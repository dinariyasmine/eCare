package com.example.data.network

import com.example.data.model.Doctor
import com.example.data.model.Patient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("/api/doctors/")
    suspend fun getDoctors(): DoctorResponse
    @GET("api/doctor/{id}/")
    suspend fun getDoctorDetailsById(@Path("id") doctorId: Int): DoctorResponse
    @GET("api/get-doctor-feedback/{doctor_id}/")
    suspend fun getDoctorFeedbackById(@Path("doctor_id") doctorId: Int): List<FeedbackResponse>
    @GET("patients")
    suspend fun getPatients(): PatientListResponse

    @GET("/api/patients/{id}")
    suspend fun getPatientById(@Path("id") id: Int): Patient
    @PUT("/api/patients/{id}/update/")
    suspend fun updatePatientById(
        @Path("id") id: Int,
        @Body updatedData: Map<String, Any?>
    ): ResponseMessage

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

}

