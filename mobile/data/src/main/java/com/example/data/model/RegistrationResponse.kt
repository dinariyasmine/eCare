package com.example.data.model

data class RegistrationResponse(
    val user: UserResponse,
    val tokens: TokenPair
)

data class TokenPair(
    val access: String,
    val refresh: String
)
