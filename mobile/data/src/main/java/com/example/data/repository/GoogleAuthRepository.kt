package com.example.data.repository


import android.util.Log
import com.example.data.model.AuthResponse
import com.example.data.model.GoogleAuthRequest
import com.example.data.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class GoogleAuthRepository {
        private val TAG = "GoogleAuthRepository"

        suspend fun authenticateWithGoogle(idToken: String): AuthResponse {
            return withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Sending Google ID token to backend")

                    // Using your existing GoogleAuthRequest data class
                    val request = GoogleAuthRequest(id_token = idToken)
                    val response = RetrofitInstance.apiService.googleAuth(request)

                    Log.d(TAG, "Google auth successful: ${response.access}")
                    response
                } catch (e: HttpException) {
                    // Get detailed error information from Retrofit's HttpException
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(TAG, "Google auth failed with code ${e.code()}, body: $errorBody", e)
                    throw Exception("Authentication failed (${e.code()}): ${errorBody ?: e.message()}")
                } catch (e: Exception) {
                    Log.e(TAG, "Google auth failed", e)
                    throw Exception("Authentication failed: ${e.message}")
                }
            }
        }
    }