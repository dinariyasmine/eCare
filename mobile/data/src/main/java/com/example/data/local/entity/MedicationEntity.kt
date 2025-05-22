package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String?,
    val created_at: String?,
    val updated_at: String?
)

