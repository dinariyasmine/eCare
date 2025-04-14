package com.example.data.repository

import com.example.data.model.Availability
import java.text.SimpleDateFormat
import java.util.*

class InMemoryAvailabilityRepository {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val availabilities = mutableListOf<Availability>()

    init {
        // Add some dummy data
        availabilities.addAll(
            listOf(
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
        )
    }

    fun getAllAvailabilities(): List<Availability> {
        return availabilities.toList()
    }

    fun getAvailabilityById(id: Int): Availability? {
        return availabilities.find { it.id == id }
    }

    fun getAvailabilitiesByDoctorId(doctorId: Int): List<Availability> {
        return availabilities.filter { it.doctor_id == doctorId }
    }

    fun getAvailabilitiesByDateRange(startDate: Date, endDate: Date): List<Availability> {
        return availabilities.filter {
            (it.start_time >= startDate && it.start_time <= endDate) ||
                    (it.end_time >= startDate && it.end_time <= endDate) ||
                    (it.start_time <= startDate && it.end_time >= endDate)
        }
    }

    fun createAvailability(availability: Availability): Boolean {
        // Check for overlapping availabilities for the same doctor
        val overlapping = availabilities.any {
            it.doctor_id == availability.doctor_id &&
                    ((availability.start_time <= it.end_time && availability.end_time >= it.start_time) ||
                            (it.start_time <= availability.end_time && it.end_time >= availability.start_time))
        }

        if (overlapping) {
            return false
        }

        return availabilities.add(availability)
    }

    fun updateAvailability(availability: Availability): Boolean {
        val index = availabilities.indexOfFirst { it.id == availability.id }
        if (index != -1) {
            // Check for overlapping availabilities for the same doctor (excluding this one)
            val overlapping = availabilities.any {
                it.id != availability.id &&
                        it.doctor_id == availability.doctor_id &&
                        ((availability.start_time <= it.end_time && availability.end_time >= it.start_time) ||
                                (it.start_time <= availability.end_time && it.end_time >= availability.start_time))
            }

            if (overlapping) {
                return false
            }

            availabilities[index] = availability
            return true
        }
        return false
    }

    fun deleteAvailability(id: Int): Boolean {
        return availabilities.removeIf { it.id == id }
    }
}
