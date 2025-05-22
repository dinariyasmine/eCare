package com.example.data.retrofit

import com.example.data.baseUrl
import com.example.data.model.Appointment
import com.example.data.model.AppointmentDto
import com.example.data.model.AppointmentRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentEndpoint {

    @GET("appointments/doctor/{id}/")
    suspend fun getAppointsByDoctor(@Path("id") id: Int): List<AppointmentDto>

    @GET("appointments/patient/{id}/")
    suspend fun getAppointsByPatient(@Path("id") id: Int): List<AppointmentDto>

    @GET("appointments/{id}/")
    suspend fun getAppointById(@Path("id") id: Int): AppointmentDto

    @POST("appointments/")
    suspend fun addAppointment(@Body data: AppointmentRequest): AppointmentDto

    @PATCH("appointments/{id}/")
    suspend fun updateAppointment(@Path("id") id: Int, @Body data: AppointmentRequest)

    @DELETE("appointments/{id}/")
    suspend fun deleteAppointment(@Path("id") id: Int)


    companion object {
        private var INSTANCE: AppointmentEndpoint? = null
        fun createInstance(): AppointmentEndpoint {
            if(INSTANCE ==null) {
                INSTANCE = Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().
                    create(AppointmentEndpoint::class.java)
            }
            return INSTANCE!!
        }
    }
}