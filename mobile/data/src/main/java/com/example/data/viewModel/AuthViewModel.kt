package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuthResponse
import com.example.data.model.RegistrationRequest
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registrationState = MutableStateFlow<AuthResponse?>(null)
    val registrationState: StateFlow<AuthResponse?> = _registrationState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    companion object {
        class Factory(private val authRepository: AuthRepository) : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                    return AuthViewModel(authRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    fun registerUser(request: RegistrationRequest, isDoctor: Boolean) {
        viewModelScope.launch {
            try {
                val response = if (isDoctor) {
                    authRepository.registerDoctor(request)
                } else {
                    authRepository.registerPatient(request)
                }
                _registrationState.value = response
            } catch (e: Exception) {
                _errorState.value = "Registration failed: ${e.message}"
            }
        }
    }

    fun clearState() {
        _registrationState.value = null
        _errorState.value = null
    }
}