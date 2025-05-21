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
    val address: String,
    val role: String,
    val birth_date: Date
)

