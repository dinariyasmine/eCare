package com.example.data.repository.inMemory

import com.example.data.model.SocialMedia

class InMemorySocialMediaRepository {
    private val socialMediaList = mutableListOf<SocialMedia>()

    init {
        // Initialize with dummy data
        socialMediaList.addAll(
            listOf(
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
        )
    }

    fun getAllSocialMedia(): List<SocialMedia> {
        return socialMediaList.toList()
    }

    fun getSocialMediaById(id: Int): SocialMedia? {
        return socialMediaList.find { it.id == id }
    }

    fun getSocialMediaByDoctorId(doctorId: Int): List<SocialMedia> {
        return socialMediaList.filter { it.doctor_id == doctorId }
    }

    fun createSocialMedia(socialMedia: SocialMedia): Boolean {
        // Check if social media with the same ID already exists
        if (socialMediaList.any { it.id == socialMedia.id }) {
            return false
        }
        return socialMediaList.add(socialMedia)
    }

    fun updateSocialMedia(socialMedia: SocialMedia): Boolean {
        val index = socialMediaList.indexOfFirst { it.id == socialMedia.id }
        if (index != -1) {
            socialMediaList[index] = socialMedia
            return true
        }
        return false
    }

    fun deleteSocialMedia(id: Int): Boolean {
        val initialSize = socialMediaList.size
        socialMediaList.removeIf { it.id == id }
        return socialMediaList.size < initialSize
    }
}
