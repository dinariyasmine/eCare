package com.example.data.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.User
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// State representation for the HomePage
sealed class HomeViewState {
    object Loading : HomeViewState()
    data class Error(val message: String) : HomeViewState()
    data class Success(val data: HomeData) : HomeViewState()
}

// Data container for the UI
data class HomeData(
    val users: List<User> = emptyList(),
    val currentUser: User? = null,
    val appointments: List<Appointment> = emptyList()
)

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val appointmentRepository: AppointmentRepository = AppointmentRepository()
) : ViewModel() {

    // Expose state as immutable StateFlow
    private val _state = MutableStateFlow<HomeViewState>(HomeViewState.Loading)
    val state: StateFlow<HomeViewState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = HomeViewState.Loading

                val data = withContext(Dispatchers.IO) {
                    // Parallel data fetching
                    val usersDeferred = async { userRepository.getAllUsers() }

                    val users = usersDeferred.await()
                    val currentUser = users.firstOrNull()

                    // Fetch appointments if user exists
                    val appointments = if (currentUser != null) {
                        appointmentRepository.getAppointmentsByPatientId(1)
                    } else {
                        emptyList()
                    }

                    HomeData(
                        users = users,
                        currentUser = currentUser,
                        appointments = appointments
                    )
                }

                _state.value = HomeViewState.Success(data)

            } catch (e: Exception) {
                _state.value = HomeViewState.Error("Failed to load data: ${e.localizedMessage}")
            }
        }
    }

    // Add function to refresh data if needed
    fun refreshData() {
        loadData()
    }
}