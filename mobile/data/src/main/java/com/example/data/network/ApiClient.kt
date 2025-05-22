package com.example.data.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// In mobile/data/src/main/java/com/example/data/network/ApiClient.kt
object ApiClient {
    private const val BASE_URL = "https://e735-105-235-130-125.ngrok-free.app"

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzQ3Nzk2ODE3LCJpYXQiOjE3NDc3OTMyMTcsImp0aSI6IjcyYWMyOTliYjE5YzRkYTM5NTE2ZGM4MmJhN2RkNjRjIiwidXNlcl9pZCI6NDR9.ukgl1JrfyaXHc0_PuUHhAUkudIOJfKV3AmyYKPJNtko")
            .method(original.method, original.body)

        chain.proceed(requestBuilder.build())
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
       // .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}

// Simple token manager
object TokenManager {
    private var token: String? = null

    fun setToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String? = token
}
