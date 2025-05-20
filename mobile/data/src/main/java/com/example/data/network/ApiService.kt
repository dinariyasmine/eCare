// mobile/data/src/main/java/com/example/data/network/ApiService.kt
package com.example.data.network

import com.example.data.model.Notification
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/notifications/")
    suspend fun getNotifications(): Response<List<Notification>>

    @GET("api/notifications/unread/")
    suspend fun getUnreadNotifications(): Response<List<Notification>>

    @POST("api/notifications/{id}/mark_as_read/")
    suspend fun markNotificationAsRead(@Path("id") notificationId: Int): Response<Map<String, String>>

    @POST("api/notifications/mark_all_as_read/")
    suspend fun markAllNotificationsAsRead(): Response<Map<String, String>>


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
