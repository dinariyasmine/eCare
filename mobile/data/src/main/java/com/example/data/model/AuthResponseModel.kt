package com.example.data.model

data class AuthResponse(
    val user: UserResponse,
    val tokens: TokenResponse
)

data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val name: String,
    val phone: String,
    val address: String,
    val role: String,
    val birth_date: String
)

data class TokenResponse(
    val refresh: String,
    val access: String
)