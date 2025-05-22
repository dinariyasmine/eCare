package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sync_status")
data class SyncStatusEntity(
    @PrimaryKey val id: Int = 1, // Single row entity
    val lastSyncTimestamp: Long = 0,
    val isSyncing: Boolean = false,
    val pendingChanges: Int = 0
)
