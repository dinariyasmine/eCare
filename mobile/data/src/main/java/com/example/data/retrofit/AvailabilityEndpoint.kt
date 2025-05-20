package com.example.data.retrofit

import com.example.data.baseUrl
import com.example.data.model.Availability
import com.example.data.model.AvailabilityRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AvailabilityEndpoint {

    @GET("availabilities/doctor/{id}/")
    suspend fun getAvailabilitiesByDoctor(@Path("id") id: Int): List<Availability>

    @POST("availabilities/")
    suspend fun addAvailability(@Body data: AvailabilityRequest)

    @PATCH("availabilities/{id}/")
    suspend fun updateAvailability(@Path("id") id: Int, @Body data: Availability)

    @DELETE("availabilties/{id}/")
    suspend fun deleteAvailability(@Path("id") id: Int)


    companion object {
        private var INSTANCE: AvailabilityEndpoint? = null
        fun createInstance(): AvailabilityEndpoint {
            if(INSTANCE ==null) {
                INSTANCE = Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().
                    create(AvailabilityEndpoint::class.java)
            }
            return INSTANCE!!
        }
    }
}