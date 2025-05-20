package com.example.data.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object TokenManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_ROLE = "user_role"

    private var prefs: SharedPreferences? = null
    private const val TAG = "TokenManager"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        Log.d(TAG, "TokenManager initialized")
    }

    fun saveToken(token: String) {
        prefs?.edit()?.putString(KEY_TOKEN, token)?.apply()
        Log.d(TAG, "Access token saved")
    }

    fun saveRefreshToken(refreshToken: String) {
        prefs?.edit()?.putString(KEY_REFRESH_TOKEN, refreshToken)?.apply()
        Log.d(TAG, "Refresh token saved")
    }

    fun saveUserId(userId: Int) {
        prefs?.edit()?.putInt(KEY_USER_ID, userId)?.apply()
        Log.d(TAG, "User ID saved: $userId")
    }

    fun saveUserRole(role: String) {
        prefs?.edit()?.putString(KEY_USER_ROLE, role)?.apply()
        Log.d(TAG, "User role saved: $role")
    }

    fun getToken(): String? {
        return prefs?.getString(KEY_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs?.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserId(): Int {
        return prefs?.getInt(KEY_USER_ID, -1) ?: -1
    }

    fun getUserRole(): String? {
        return prefs?.getString(KEY_USER_ROLE, null)
    }

    fun isUserLoggedIn(): Boolean {
        val token = getToken()
        val userId = getUserId()
        val isLoggedIn = !token.isNullOrEmpty() && userId != -1
        Log.d(TAG, "Checking if user is logged in: $isLoggedIn")
        return isLoggedIn
    }

    fun clearTokens() {
        prefs?.edit()
            ?.remove(KEY_TOKEN)
            ?.remove(KEY_REFRESH_TOKEN)
            ?.remove(KEY_USER_ID)
            ?.remove(KEY_USER_ROLE)
            ?.apply()
        Log.d(TAG, "All auth data cleared")
    }
}