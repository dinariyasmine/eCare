package com.example.data.model

data class GoogleAuthRequest(
    val id_token: String,
    val client_id: String? = null
)