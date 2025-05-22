package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "prescription_items",
    foreignKeys = [
        ForeignKey(
            entity = PrescriptionEntity::class,
            parentColumns = ["localId"],
            childColumns = ["prescriptionLocalId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("prescriptionLocalId"),
        Index("medicationId")
    ]
)
data class PrescriptionItemEntity(
    @PrimaryKey val id: Int? = null,
    val localId: String = UUID.randomUUID().toString(),
    val prescriptionLocalId: String,
    val medicationId: Int,
    val dosage: String,
    val duration: String,
    val frequency: String,
    val instructions: String,
    val isSynced: Boolean = false
)
