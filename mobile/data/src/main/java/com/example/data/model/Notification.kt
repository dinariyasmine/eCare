package com.example.data.model

import java.util.Date

data class Notification(
    val id: Int,
    val title: String,
    val description: String,
    val user_id: Int,
    val date_creation: Date,
    val time_creation: Date,
    val type: String
)