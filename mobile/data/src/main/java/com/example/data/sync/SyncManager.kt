package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

class SyncManager(private val context: Context) {

    fun scheduleSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES,  // Minimum interval is 15 minutes
            5, TimeUnit.MINUTES    // Flex interval
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "prescription_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )

        Log.d("SyncManager", "Sync work scheduled")
    }

    fun requestImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "immediate_prescription_sync",
            ExistingWorkPolicy.REPLACE,
            syncWorkRequest
        )

        Log.d("SyncManager", "Immediate sync requested")
    }
}
