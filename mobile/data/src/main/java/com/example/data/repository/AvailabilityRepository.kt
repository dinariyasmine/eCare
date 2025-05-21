package com.example.data.repository

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.data.model.Availability
import com.example.data.model.AvailabilityDto
import com.example.data.model.AvailabilityRequest
import com.example.data.retrofit.AvailabilityEndpoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class AvailabilityRepository(private val availabilityEndpoint: AvailabilityEndpoint) {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    // Helper function to convert DTO to Availability
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertDtoToAvailability(dto: AvailabilityDto): Availability {
        return Availability(
            id = dto.id,
            booked = dto.booked,
            doctor_id = dto.doctor_id,
            start_time = LocalDateTime.parse(dto.start_time, formatter),
            end_time = LocalDateTime.parse(dto.end_time, formatter)
        )
    }

    // Fetches availabilities by doctor (converts DTO to Availability)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAvailabilitiesByDoctor(id: Int): List<Availability> {
        return withContext(Dispatchers.IO) {
            try {
                val response = availabilityEndpoint.getAvailabilitiesByDoctor(id)
                response.map { convertDtoToAvailability(it) }
            } catch (e: Exception) {
                println("Error: $e")
                throw e
            }
        }
    }

    // Creates an availability (handles API + local DB)
    suspend fun createAvailability(availability: AvailabilityRequest): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = availability.copy(
                    start_time = availability.start_time.format(formatter),
                    end_time = availability.end_time.format(formatter)
                )
                availabilityEndpoint.addAvailability(request)

                true
            } catch (e: Exception) {
                println("Error: $e")
                throw e
            }
        }
    }

    // Updates an availability
    suspend fun updateAvailability(id: Int, availability: Availability): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                availabilityEndpoint.updateAvailability(id, availability)
                true
            } catch (e: Exception) {
                println("Error: $e")
                throw e
            }
        }
    }

    // Deletes an availability
    suspend fun deleteAvailability(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                availabilityEndpoint.deleteAvailability(id)
                true
            } catch (e: Exception) {
                println("Error: $e")
                throw e
            }
        }
    }
}