package com.example.data.repository

import android.annotation.SuppressLint
import com.example.data.model.Availability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class AvailabilityRepository {
    @SuppressLint("SimpleDateFormat")
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<Availability> = withContext(Dispatchers.IO) {
        // Simulate network delay
        Thread.sleep(1000)
        return@withContext listOf(
            Availability(
                id = 1,
                doctor_id = 101,
                start_time = sdf.parse("2025-04-15 09:00:00") as Date,
                end_time = sdf.parse("2025-04-15 17:00:00") as Date
            ),
            Availability(
                id = 2,
                doctor_id = 102,
                start_time = sdf.parse("2025-04-16 10:00:00") as Date,
                end_time = sdf.parse("2025-04-16 18:00:00") as Date
            ),
            Availability(
                id = 3,
                doctor_id = 101,
                start_time = sdf.parse("2025-04-17 09:00:00") as Date,
                end_time = sdf.parse("2025-04-17 17:00:00") as Date
            ),
            Availability(
                id = 4,
                doctor_id = 103,
                start_time = sdf.parse("2025-04-18 08:30:00") as Date,
                end_time = sdf.parse("2025-04-18 16:30:00") as Date
            )
        )
    }

    suspend fun getAllAvailabilities(): List<Availability> {
        return simulateDatabaseCall()
    }

    suspend fun getAvailabilityById(id: Int): Availability? {
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun getAvailabilitiesByDoctorId(doctorId: Int): List<Availability> {
        return simulateDatabaseCall().filter { it.doctor_id == doctorId }
    }

    suspend fun getAvailabilitiesByDateRange(startDate: Date, endDate: Date): List<Availability> {
        return simulateDatabaseCall().filter {
            (it.start_time >= startDate && it.start_time <= endDate) ||
                    (it.end_time >= startDate && it.end_time <= endDate) ||
                    (it.start_time <= startDate && it.end_time >= endDate)
        }
    }

    suspend fun createAvailability(availability: Availability): Boolean {
        // Simulate creating an availability in the database
        Thread.sleep(500)

        // Check for overlapping availabilities
        val existingAvailabilities = getAvailabilitiesByDoctorId(availability.doctor_id)
        val overlapping = existingAvailabilities.any {
            (availability.start_time <= it.end_time && availability.end_time >= it.start_time) ||
                    (it.start_time <= availability.end_time && it.end_time >= availability.start_time)
        }

        return !overlapping
    }

    suspend fun updateAvailability(availability: Availability): Boolean {
        // Simulate updating an availability in the database
        withContext(Dispatchers.IO) {
            Thread.sleep(500)
        }

        // Check for overlapping availabilities (excluding this one)
        val existingAvailabilities = getAvailabilitiesByDoctorId(availability.doctor_id)
        val overlapping = existingAvailabilities.any {
            it.id != availability.id &&
                    ((availability.start_time <= it.end_time && availability.end_time >= it.start_time) ||
                            (it.start_time <= availability.end_time && it.end_time >= availability.start_time))
        }

        return !overlapping
    }

    suspend fun deleteAvailability(id: Int): Boolean {
        // Simulate deleting an availability from the database
        withContext(Dispatchers.IO) {
            Thread.sleep(500)
        }
        return true
    }
}
