package com.example.data.network

import android.telecom.Call
import com.example.data.model.Doctor
import com.example.data.model.DoctorDetails
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/api/doctors/")
    suspend fun getDoctors(): DoctorResponse
    @GET("api/doctor/{id}/")
    suspend fun getDoctorDetailsById(@Path("id") doctorId: Int): DoctorResponse
    @GET("api/get-doctor-feedback/{doctor_id}/")
    suspend fun getDoctorFeedbackById(@Path("doctor_id") doctorId: Int): List<FeedbackResponse>
}
