package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Feedback
import com.example.data.repository.FeedbackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FeedbackViewModel(private val feedbackRepository: FeedbackRepository) : ViewModel() {

    private val _feedbacks = MutableStateFlow<List<Feedback>>(emptyList())
    val feedbacks: StateFlow<List<Feedback>> get() = _feedbacks

    private val _selectedFeedback = MutableStateFlow<Feedback?>(null)
    val selectedFeedback: StateFlow<Feedback?> get() = _selectedFeedback

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val timeSdf = SimpleDateFormat("HH:mm:ss")

    init {
        fetchAllFeedbacks()
    }

    fun fetchAllFeedbacks() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _feedbacks.value = feedbackRepository.getAllFeedbacks()
            } catch (e: Exception) {
                _error.value = "Failed to load feedbacks: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getFeedbackById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val feedback = feedbackRepository.getFeedbackById(id)
                if (feedback != null) {
                    _selectedFeedback.value = feedback
                } else {
                    _error.value = "Feedback not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get feedback: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getFeedbacksByPatientId(patientId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _feedbacks.value = feedbackRepository.getFeedbacksByPatientId(patientId)
            } catch (e: Exception) {
                _error.value = "Failed to load patient feedbacks: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getFeedbacksByDoctorId(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _feedbacks.value = feedbackRepository.getFeedbacksByDoctorId(doctorId)
            } catch (e: Exception) {
                _error.value = "Failed to load doctor feedbacks: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getFeedbacksByDate(dateString: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val date = sdf.parse(dateString)
                if (date != null) {
                    _feedbacks.value = feedbackRepository.getFeedbacksByDate(date)
                } else {
                    _error.value = "Invalid date format"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load feedbacks by date: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createFeedback(
        title: String,
        description: String,
        patientId: Int,
        doctorId: Int
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate input
                if (title.isBlank()) {
                    _error.value = "Title cannot be empty"
                    _loading.value = false
                    return@launch
                }

                if (description.isBlank()) {
                    _error.value = "Description cannot be empty"
                    _loading.value = false
                    return@launch
                }

                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_feedbacks.value.maxOfOrNull { it.id } ?: 0) + 1

                // Get current date and time
                val currentDate = Date()
                val currentTime = Date()

                val newFeedback = Feedback(
                    id = newId,
                    title = title,
                    description = description,
                    patient_id = patientId,
                    doctor_id = doctorId,
                    date_creation = currentDate,
                    time_creation = currentTime
                )

                val success = feedbackRepository.createFeedback(newFeedback)
                if (success) {
                    fetchAllFeedbacks() // Refresh the list
                } else {
                    _error.value = "Failed to create feedback: Feedback with the same ID already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create feedback: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateFeedback(feedback: Feedback) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate input
                if (feedback.title.isBlank()) {
                    _error.value = "Title cannot be empty"
                    _loading.value = false
                    return@launch
                }

                if (feedback.description.isBlank()) {
                    _error.value = "Description cannot be empty"
                    _loading.value = false
                    return@launch
                }

                val success = feedbackRepository.updateFeedback(feedback)
                if (success) {
                    fetchAllFeedbacks() // Refresh the list
                    if (_selectedFeedback.value?.id == feedback.id) {
                        _selectedFeedback.value = feedback // Update selected feedback if it was selected
                    }
                } else {
                    _error.value = "Failed to update feedback: Feedback not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update feedback: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteFeedback(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = feedbackRepository.deleteFeedback(id)
                if (success) {
                    fetchAllFeedbacks() // Refresh the list
                    if (_selectedFeedback.value?.id == id) {
                        _selectedFeedback.value = null // Clear selected feedback if it was deleted
                    }
                } else {
                    _error.value = "Failed to delete feedback: Feedback not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete feedback: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchFeedbacksByTitle(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _feedbacks.value = feedbackRepository.searchFeedbacksByTitle(query)
            } catch (e: Exception) {
                _error.value = "Failed to search feedbacks by title: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchFeedbacksByDescription(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _feedbacks.value = feedbackRepository.searchFeedbacksByDescription(query)
            } catch (e: Exception) {
                _error.value = "Failed to search feedbacks by description: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedFeedback() {
        _selectedFeedback.value = null
    }

    // Factory class to provide FeedbackRepository dependency
    class Factory(private val feedbackRepository: FeedbackRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedbackViewModel::class.java)) {
                return FeedbackViewModel(feedbackRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
