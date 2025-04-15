package com.example.data.model

import java.io.Serializable
import java.util.Date

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val adress: String,
    val role: Role,
    val birth_date: Date
)

enum class Role {
    ADMIN,
    PATIENT,
    DOCTOR
}