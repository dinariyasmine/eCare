package com.example.data.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.local.database.AppDatabase
import com.example.data.repository.PrescriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("SyncWorker", "Starting sync process")

            val repository = PrescriptionRepository.getInstance(applicationContext)
            val syncResult = repository.syncPrescriptions()

            return@withContext if (syncResult) {
                Log.d("SyncWorker", "Sync completed successfully")
                Result.success()
            } else {
                Log.d("SyncWorker", "Sync failed, will retry")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error during sync: ${e.message}", e)
            Result.failure()
        }
    }
}
