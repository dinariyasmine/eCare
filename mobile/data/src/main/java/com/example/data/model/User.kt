package com.example.data.model

import java.io.Serializable
import java.util.Date

data class User
    (
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val address: String, // Note: API uses "address" not "adress"
    val role: String,    // API uses string not enum
    val birth_date: Date
)

