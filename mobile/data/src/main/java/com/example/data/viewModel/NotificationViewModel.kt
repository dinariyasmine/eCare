// mobile/data/src/main/java/com/example/data/viewModel/NotificationViewModel.kt
package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Notification
import com.example.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class NotificationViewModel(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(NotificationTab.UNREAD)
    val selectedTab: StateFlow<NotificationTab> = _selectedTab.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (_selectedTab.value) {
                NotificationTab.UNREAD -> {
                    notificationRepository.getUnreadNotifications().collectLatest { notifications ->
                        _uiState.value = _uiState.value.copy(
                            notifications = groupNotificationsByDate(notifications),
                            isLoading = false
                        )
                    }
                }
                NotificationTab.READ -> {
                    notificationRepository.getNotifications().collectLatest { allNotifications ->
                        val readNotifications = allNotifications.filter { it.isRead }
                        _uiState.value = _uiState.value.copy(
                            notifications = groupNotificationsByDate(readNotifications),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun selectTab(tab: NotificationTab) {
        _selectedTab.value = tab
        loadNotifications()
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val success = notificationRepository.markAsRead(notificationId)
            if (success) {
                loadNotifications()
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val success = notificationRepository.markAllAsRead()
            if (success) {
                loadNotifications()
            }
        }
    }

    private fun groupNotificationsByDate(notifications: List<Notification>): Map<String, List<Notification>> {
        return notifications.groupBy { notification ->
            when {
                isSameDay(notification.timestamp, Date()) -> "Today"
                isYesterday(notification.timestamp) -> "Yesterday"
                else -> "Older"
            }
        }
    }

    // Helper functions for date comparison
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { time = date1 }
        val cal2 = java.util.Calendar.getInstance().apply { time = date2 }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(date: Date): Boolean {
        val cal1 = java.util.Calendar.getInstance().apply { time = date }
        val cal2 = java.util.Calendar.getInstance()
        cal2.add(java.util.Calendar.DAY_OF_YEAR, -1)
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }
}

data class NotificationUiState(
    val notifications: Map<String, List<Notification>> = emptyMap(),
    val isLoading: Boolean = true,
    val error: String? = null
)

enum class NotificationTab {
    UNREAD, READ
}
