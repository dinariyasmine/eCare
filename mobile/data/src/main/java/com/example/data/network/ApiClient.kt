package com.example.data.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://ea18-105-102-48-10.ngrok-free.app"

    // Custom deserializer for handling both User objects and user IDs
    class AnyDeserializer : JsonDeserializer<Any> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Any {
            return if (json.isJsonPrimitive) {
                if (json.asJsonPrimitive.isNumber) {
                    json.asInt
                } else {
                    json.asString
                }
            } else {
                context.deserialize(json, Map::class.java)
            }
        }
    }

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .registerTypeAdapter(Any::class.java, AnyDeserializer())
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzQ3ODg0OTg3LCJpYXQiOjE3NDc4ODEzODcsImp0aSI6ImMzNjFkZGJhNGVhZTQwYjhhZWM5NzZlMWQzNGQ0ZmNhIiwidXNlcl9pZCI6Njl9.5zun11QgHQUjOST3nux1Iwtj8Vd0juLs3Xt0wlZOxOg")
            .method(original.method, original.body)
        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
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
