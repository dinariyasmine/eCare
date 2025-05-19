package com.example.data.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthResponse
import com.example.data.model.LoginRequest
import com.example.data.model.RegistrationRequest
import com.example.data.model.UserResponse
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    // Login state
    private val _loginState = MutableStateFlow<AuthResponse?>(null)
    val loginState: StateFlow<AuthResponse?> = _loginState

    // Registration state
    private val _registrationState = MutableStateFlow<AuthResponse?>(null)
    val registrationState: StateFlow<AuthResponse?> = _registrationState

    // Error state
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    // Current user state for transferring data between screens
    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser

    // Store password temporarily for doctor registration (securely in production)
    private val _currentPassword = MutableStateFlow<String?>(null)
    val currentPassword: StateFlow<String?> = _currentPassword
    fun login(request: LoginRequest) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(request.username, request.password)
                _loginState.value = response
                _currentUser.value = response.user
            } catch (e: Exception) {
                _errorState.value = e.message ?: "Login failed"
            }
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }


    fun registerUser(request: RegistrationRequest, isDoctor: Boolean) {
        viewModelScope.launch {
            try {
                if (!isDoctor) {
                    // Register patient
                    val response = authRepository.registerPatient(request)
                    _registrationState.value = response
                } else {
                    // For doctors, store the basic info for the second screen
                    // In production, this would use a more secure approach
                    storeUserData(request)
                }
            } catch (e: Exception) {
                _errorState.value = e.message ?: "Registration failed"
            }
        }
    }

    fun storeUserData(request: RegistrationRequest) {
        Log.d("AuthViewModel", "Storing user data: ${request.username}, ${request.email}")

        // Store user data to be used in the second registration step
        _currentUser.value = UserResponse(
            id = 0,  // Will be assigned by the server
            username = request.username,
            email = request.email,
            name = request.name,
            phone = request.phone,
            address = request.address,
            role = "doctor",
            birth_date = request.birth_date
        )
        _currentPassword.value = request.password

        Log.d("AuthViewModel", "User data stored: ${_currentUser.value?.name}, ${_currentUser.value?.email}")
        Log.d("AuthViewModel", "Password stored: ${_currentPassword.value?.substring(0, 1)}****")

        // For better diagnosis, add a debug method to check if data is available
        checkStoredData()
    }

    // Debug method to verify data is stored
    private fun checkStoredData() {
        Log.d("AuthViewModel", "Current stored user data:")
        Log.d("AuthViewModel", "User is null? ${_currentUser.value == null}")
        if (_currentUser.value != null) {
            Log.d("AuthViewModel", "Username: ${_currentUser.value?.username}")
            Log.d("AuthViewModel", "Email: ${_currentUser.value?.email}")
            Log.d("AuthViewModel", "Name: ${_currentUser.value?.name}")
        }
        Log.d("AuthViewModel", "Password is null? ${_currentPassword.value == null}")
    }

    fun registerDoctor(request: RegistrationRequest) {
        Log.d("AuthViewModel", "Starting doctor registration: ${request.username}, specialty: ${request.specialty}, clinic: ${request.clinic_id}")

        // Debug checks before registration
        checkStoredData()

        viewModelScope.launch {
            try {
                val response = authRepository.registerDoctor(request)
                Log.d("AuthViewModel", "Doctor registration successful")
                _registrationState.value = response
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Doctor registration failed", e)
                _errorState.value = e.message ?: "Doctor registration failed"
            }
        }
    }

    fun clearState() {
        Log.d("AuthViewModel", "Clearing registration and error state")
        _registrationState.value = null
        _errorState.value = null
    }

    fun clearRegistrationState() {
        Log.d("AuthViewModel", "Clearing only registration state")
        _registrationState.value = null
    }

    fun clearUserData() {
        Log.d("AuthViewModel", "Clearing user data")
        _currentUser.value = null
        _currentPassword.value = null
    }



    companion object {
        class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    return AuthViewModel(authRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}