package com.example.data.repository

import com.example.data.model.SocialMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SocialMediaRepository {
    // Simulating a database call with a delay
    private suspend fun simulateDatabaseCall(): List<SocialMedia> = withContext(Dispatchers.IO) {
        // Simulate network delay
        delay(1000)
        return@withContext listOf(
            SocialMedia(
                id = 1,
                doctor_id = 101,
                name = "LinkedIn",
                link = "https://linkedin.com/in/doctor101"
            ),
            SocialMedia(
                id = 2,
                doctor_id = 102,
                name = "Twitter",
                link = "https://twitter.com/doctor102"
            ),
            SocialMedia(
                id = 3,
                doctor_id = 103,
                name = "Facebook",
                link = "https://facebook.com/doctor103"
            )
        )
    }

    suspend fun getAllSocialMedia(): List<SocialMedia> {
        return simulateDatabaseCall()
    }

    suspend fun getSocialMediaById(id: Int): SocialMedia? {
        delay(500) // Simulate delay
        return simulateDatabaseCall().find { it.id == id }
    }

    suspend fun getSocialMediaByDoctorId(doctorId: Int): List<SocialMedia> {
        delay(500) // Simulate delay
        return simulateDatabaseCall().filter { it.doctor_id == doctorId }
    }

    suspend fun createSocialMedia(socialMedia: SocialMedia): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would insert into a database
        // For now, we'll just simulate success or failure
        return !simulateDatabaseCall().any { it.id == socialMedia.id }
    }

    suspend fun updateSocialMedia(socialMedia: SocialMedia): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would update the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == socialMedia.id }
    }

    suspend fun deleteSocialMedia(id: Int): Boolean {
        delay(500) // Simulate delay
        // In a real implementation, this would delete from the database
        // For now, we'll just simulate success or failure
        return simulateDatabaseCall().any { it.id == id }
    }
}
