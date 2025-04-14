package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Notification
import com.example.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NotificationViewModel(private val notificationRepository: NotificationRepository) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> get() = _notifications

    private val _selectedNotification = MutableStateFlow<Notification?>(null)
    val selectedNotification: StateFlow<Notification?> get() = _selectedNotification

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val sdf = SimpleDateFormat("yyyy-MM-dd")
    private val timeSdf = SimpleDateFormat("HH:mm:ss")

    init {
        fetchAllNotifications()
    }

    fun fetchAllNotifications() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _notifications.value = notificationRepository.getAllNotifications()
            } catch (e: Exception) {
                _error.value = "Failed to load notifications: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getNotificationById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val notification = notificationRepository.getNotificationById(id)
                if (notification != null) {
                    _selectedNotification.value = notification
                } else {
                    _error.value = "Notification not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get notification: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getNotificationsByUserId(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _notifications.value = notificationRepository.getNotificationsByUserId(userId)
            } catch (e: Exception) {
                _error.value = "Failed to load user notifications: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createNotification(
        title: String,
        description: String,
        userId: Int,
        type: String
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

                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_notifications.value.maxOfOrNull { it.id } ?: 0) + 1

                // Get current date and time
                val currentDate = Date()
                val currentTime = Date()

                val newNotification = Notification(
                    id = newId,
                    title = title,
                    description = description,
                    user_id = userId,
                    date_creation = currentDate,
                    time_creation = currentTime,
                    type = type
                )

                val success = notificationRepository.createNotification(newNotification)
                if (success) {
                    fetchAllNotifications() // Refresh the list
                } else {
                    _error.value = "Failed to create notification: Notification with the same ID already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create notification: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateNotification(notification: Notification) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Validate input
                if (notification.title.isBlank()) {
                    _error.value = "Title cannot be empty"
                    _loading.value = false
                    return@launch
                }

                val success = notificationRepository.updateNotification(notification)
                if (success) {
                    fetchAllNotifications() // Refresh the list
                    if (_selectedNotification.value?.id == notification.id) {
                        _selectedNotification.value = notification // Update selected notification if it was selected
                    }
                } else {
                    _error.value = "Failed to update notification: Notification not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update notification: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteNotification(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = notificationRepository.deleteNotification(id)
                if (success) {
                    fetchAllNotifications() // Refresh the list
                    if (_selectedNotification.value?.id == id) {
                        _selectedNotification.value = null // Clear selected notification if it was deleted
                    }
                } else {
                    _error.value = "Failed to delete notification: Notification not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete notification: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getNotificationsByType(type: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _notifications.value = notificationRepository.getNotificationsByType(type)
            } catch (e: Exception) {
                _error.value = "Failed to load notifications by type: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getRecentNotifications(limit: Int = 5) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _notifications.value = notificationRepository.getRecentNotifications(limit)
            } catch (e: Exception) {
                _error.value = "Failed to load recent notifications: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedNotification() {
        _selectedNotification.value = null
    }

    // Factory class to provide NotificationRepository dependency
    class Factory(private val notificationRepository: NotificationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                return NotificationViewModel(notificationRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
