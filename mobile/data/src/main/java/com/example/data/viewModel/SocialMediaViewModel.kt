package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.SocialMedia
import com.example.data.repository.SocialMediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SocialMediaViewModel(private val socialMediaRepository: SocialMediaRepository) : ViewModel() {

    private val _socialMedia = MutableStateFlow<List<SocialMedia>>(emptyList())
    val socialMedia: StateFlow<List<SocialMedia>> get() = _socialMedia

    private val _selectedSocialMedia = MutableStateFlow<SocialMedia?>(null)
    val selectedSocialMedia: StateFlow<SocialMedia?> get() = _selectedSocialMedia

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        fetchAllSocialMedia()
    }

    fun fetchAllSocialMedia() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _socialMedia.value = socialMediaRepository.getAllSocialMedia()
            } catch (e: Exception) {
                _error.value = "Failed to load social media: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getSocialMediaById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val socialMedia = socialMediaRepository.getSocialMediaById(id)
                if (socialMedia != null) {
                    _selectedSocialMedia.value = socialMedia
                } else {
                    _error.value = "Social media not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get social media: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getSocialMediaByDoctorId(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _socialMedia.value = socialMediaRepository.getSocialMediaByDoctorId(doctorId)
            } catch (e: Exception) {
                _error.value = "Failed to load social media for doctor: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createSocialMedia(
        doctorId: Int,
        name: String,
        link: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate input
                if (name.isBlank()) {
                    _error.value = "Name cannot be empty"
                    _loading.value = false
                    return@launch
                }

                if (link.isBlank()) {
                    _error.value = "Link cannot be empty"
                    _loading.value = false
                    return@launch
                }

                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_socialMedia.value.maxOfOrNull { it.id } ?: 0) + 1

                val newSocialMedia = SocialMedia(
                    id = newId,
                    doctor_id = doctorId,
                    name = name,
                    link = link
                )

                val success = socialMediaRepository.createSocialMedia(newSocialMedia)
                if (success) {
                    fetchAllSocialMedia() // Refresh the list
                } else {
                    _error.value = "Failed to create social media: Social media with the same ID already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create social media: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateSocialMedia(socialMedia: SocialMedia) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate input
                if (socialMedia.name.isBlank()) {
                    _error.value = "Name cannot be empty"
                    _loading.value = false
                    return@launch
                }

                if (socialMedia.link.isBlank()) {
                    _error.value = "Link cannot be empty"
                    _loading.value = false
                    return@launch
                }

                val success = socialMediaRepository.updateSocialMedia(socialMedia)
                if (success) {
                    fetchAllSocialMedia() // Refresh the list
                    if (_selectedSocialMedia.value?.id == socialMedia.id) {
                        _selectedSocialMedia.value = socialMedia // Update selected social media if it was selected
                    }
                } else {
                    _error.value = "Failed to update social media: Social media not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update social media: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteSocialMedia(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = socialMediaRepository.deleteSocialMedia(id)
                if (success) {
                    fetchAllSocialMedia() // Refresh the list
                    if (_selectedSocialMedia.value?.id == id) {
                        _selectedSocialMedia.value = null // Clear selected social media if it was deleted
                    }
                } else {
                    _error.value = "Failed to delete social media: Social media not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete social media: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedSocialMedia() {
        _selectedSocialMedia.value = null
    }

    // Factory class to provide SocialMediaRepository dependency
    class Factory(private val socialMediaRepository: SocialMediaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SocialMediaViewModel::class.java)) {
                return SocialMediaViewModel(socialMediaRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
