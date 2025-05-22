package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.data.local.database.Converters
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "prescriptions",
    indices = [Index(value = ["localId"], unique = true)]
)
data class PrescriptionEntity(
    @PrimaryKey val id: Int? = null, // Null if created locally and not synced
    val localId: String = UUID.randomUUID().toString(), // Used for local identification
    val patient: Int,
    val doctor: Int,
    val date: String,
    val notes: String?,
    val pdfFile: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val isSynced: Boolean = false,
    val pendingOperation: String? = null // CREATE, UPDATE, DELETE
)

