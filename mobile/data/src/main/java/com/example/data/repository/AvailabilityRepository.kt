package com.example.data.repository

import com.example.data.model.Availability
import com.example.data.model.AvailabilityRequest
import com.example.data.retrofit.AvailabilityEndpoint

class AvailabilityRepository (private val availabilityEndpoint: AvailabilityEndpoint) {

    suspend fun getAvailabilitiesByDoctor(id: Int): List<Availability> {
       val availabilities = availabilityEndpoint.getAvailabilitiesByDoctor(id)
        return availabilities
    }

    suspend fun createAvailability(availability: AvailabilityRequest): Boolean {
        availabilityEndpoint.addAvailability(availability)
        return true
    }

    suspend fun updateAvailability(id: Int, availability: Availability): Boolean {
        availabilityEndpoint.updateAvailability(id,availability)
        return true
    }

    suspend fun deleteAvailability(id: Int): Boolean {
        availabilityEndpoint.deleteAvailability(id)
        return true
    }
}
