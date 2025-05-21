package com.example.data.model

data class AuthResponse(
    val refresh: String?,
    val access: String?,
    val user: UserResponse?
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