package com.example.data.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Feedback
import com.example.data.network.SubmitFeedbackRequest
import com.example.data.repository.FeedbackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FeedbackViewModel(
    private val repository: FeedbackRepository
) : ViewModel() {
    private val _feedbacks = MutableStateFlow<List<Feedback>>(emptyList())
    val feedbacks: StateFlow<List<Feedback>> = _feedbacks.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedFeedback = MutableStateFlow<Feedback?>(null)
    val selectedFeedback: StateFlow<Feedback?> get() = _selectedFeedback

    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val timeSdf = SimpleDateFormat("HH:mm:ss")

    init {
       // fetchAllFeedbacks()
    }

//    fun fetchAllFeedbacks() {
//        viewModelScope.launch {
//            _loading.value = true
//            _error.value = null
//
//            try {
//                _feedbacks.value = feedbackRepository.
//            } catch (e: Exception) {
//                _error.value = "Failed to load feedbacks: ${e.message}"
//            } finally {
//                _loading.value = false
//            }
//        }
//    }



//    fun getFeedbacksByPatientId(patientId: Int) {
//        viewModelScope.launch {
//            _loading.value = true
//            _error.value = null
//
//            try {
//                _feedbacks.value = feedbackRepository.getFeedbacksByPatientId(patientId)
//            } catch (e: Exception) {
//                _error.value = "Failed to load patient feedbacks: ${e.message}"
//            } finally {
//                _loading.value = false
//            }
//        }
//    }

    fun getFeedbacksByDoctorId(doctorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val result = repository.getFeedbacksByDoctorId(doctorId)
                _feedbacks.value = result
                Log.d("FeedbackViewModel", "Fetched ${result.size} feedbacks for doctor $doctorId")
            } catch (e: Exception) {
                Log.e("FeedbackViewModel", "Error fetching feedbacks: ${e.message}", e)
                _error.value = "Failed to load doctor feedbacks: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    fun submitFeedback(doctorId: Int, feedbackRequest: SubmitFeedbackRequest, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = repository.submitFeedback(doctorId, feedbackRequest)
                if (!success) {
                    _error.value = "Failed to submit feedback"
                }
                onResult(success)
            } catch (e: Exception) {
                Log.e("FeedbackViewModel", "Error submitting feedback: ${e.message}", e)
                _error.value = "Error submitting feedback: ${e.message}"
                onResult(false)
            } finally {
                _loading.value = false
            }
        }
    }

    // Factory class to provide FeedbackRepository dependency
    class Factory(private val repository: FeedbackRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedbackViewModel::class.java)) {
                return FeedbackViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
