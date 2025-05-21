package com.example.data.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AuthRepository
import com.example.data.repository.DoctorRepository
import com.example.data.repository.GoogleAuthRepository
import com.example.data.repository.SocialMediaRepository
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    // Login state
    private val _loginState = MutableStateFlow<AuthResponse?>(null)
    val loginState: StateFlow<AuthResponse?> = _loginState
    private val socialMediaRepository: SocialMediaRepository = SocialMediaRepository()
    private val doctorRepository: DoctorRepository = DoctorRepository()
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

    // Password Reset States
    private val _passwordResetRequestState = MutableStateFlow<MessageResponse?>(null)
    val passwordResetRequestState: StateFlow<MessageResponse?> = _passwordResetRequestState

    private val _otpVerificationState = MutableStateFlow<MessageResponse?>(null)
    val otpVerificationState: StateFlow<MessageResponse?> = _otpVerificationState

    private val _passwordResetState = MutableStateFlow<MessageResponse?>(null)
    val passwordResetState: StateFlow<MessageResponse?> = _passwordResetState

    private val googleAuthRepository: GoogleAuthRepository = GoogleAuthRepository()
    fun login(request: LoginRequest) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(request.username, request.password)

                // Debug the response
                Log.d("AuthViewModel", "Login response received: $response")
                Log.d("AuthViewModel", "Access token: ${response.access}")
                Log.d("AuthViewModel", "User: ${response.user}")

                _loginState.value = response
                _currentUser.value = response.user
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
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

                    storeUserData(request)
                }
            } catch (e: Exception) {
                _errorState.value = e.message ?: "Registration failed"
            }
        }
    }
//was used when we had two screens
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


        checkStoredData()
    }

    // verify data is stored
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

    fun registerDoctor(request: RegistrationRequest, socialMediaLinks: List<SocialMedia> = emptyList()) {
        viewModelScope.launch {
            _registrationState.value = null
            _errorState.value = null

            try {
                // Register the doctor first
                val response = authRepository.registerDoctor(request)
                _registrationState.value = response

                // If doctor registration is successful and we have social media links to save
                if (response.access != null && socialMediaLinks.isNotEmpty()) {
                    // Get userId from the response
                    val userId = response.user?.id ?: return@launch

                    Log.d("AuthViewModel", "User registered successfully with ID: $userId, retrieving doctor profile")

                    // Fetch the doctor ID using userId - with a small delay to ensure backend is ready
                    kotlinx.coroutines.delay(1000)

                    try {
                        val doctorId = doctorRepository.getDoctorByUserId(userId)

                        if (doctorId != null) {
                            Log.d("AuthViewModel", "Retrieved doctor ID: $doctorId from user ID: $userId")

                            // Add each social media link with the doctor's ID
                            for (socialMedia in socialMediaLinks) {
                                // Create a new social media object with the correct doctor_id
                                val updatedSocialMedia = socialMedia.copy(doctor_id = doctorId)
                                Log.d("AuthViewModel", "Creating social media: $updatedSocialMedia")

                                val success = socialMediaRepository.createSocialMedia(updatedSocialMedia)
                                if (success) {
                                    Log.d("AuthViewModel", "Successfully created social media entry")
                                } else {
                                    Log.e("AuthViewModel", "Failed to create social media entry")
                                }
                            }

                            Log.d("AuthViewModel", "Finished processing ${socialMediaLinks.size} social media links")
                        } else {
                            Log.e("AuthViewModel", "Could not retrieve doctor ID for user ID: $userId")
                            _errorState.value = "Registration successful but failed to retrieve doctor profile"
                        }
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Failed to get doctor profile or add social media", e)
                        _errorState.value = "Registration successful but failed to add social media: ${e.message}"
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Doctor registration failed", e)
                _errorState.value = e.message ?: "An error occurred during registration"
            }
        }
    }


    // Password Reset Functions
    fun requestPasswordReset(email: String) {
        Log.d("AuthViewModel", "Requesting password reset for email: $email")
        viewModelScope.launch {
            try {
                val response = authRepository.requestPasswordReset(email)
                Log.d("AuthViewModel", "Password reset request successful: ${response.message}")
                _passwordResetRequestState.value = response
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Password reset request failed", e)
                _errorState.value = e.message ?: "Password reset request failed"
            }
        }
    }

    fun verifyOtp(email: String, otpCode: String) {
        Log.d("AuthViewModel", "Verifying OTP for email: $email")
        viewModelScope.launch {
            try {
                val response = authRepository.verifyOtp(email, otpCode)
                Log.d("AuthViewModel", "OTP verification successful: ${response.message}")
                _otpVerificationState.value = response
            } catch (e: Exception) {
                Log.e("AuthViewModel", "OTP verification failed", e)
                _errorState.value = e.message ?: "OTP verification failed"
            }
        }
    }

    fun resetPassword(email: String, otpCode: String, password: String, password2: String) {
        Log.d("AuthViewModel", "Resetting password for email: $email")
        viewModelScope.launch {
            try {
                val response = authRepository.resetPassword(email, otpCode, password, password2)
                Log.d("AuthViewModel", "Password reset successful: ${response.message}")
                _passwordResetState.value = response
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is HttpException -> {
                        try {
                            // Get raw error body as string
                            val errorBody = e.response()?.errorBody()?.string()
                            Log.e("AuthViewModel", "HTTP Error Body: $errorBody")
                            errorBody ?: "HTTP ${e.code()} error"
                        } catch (ex: Exception) {
                            "Failed to parse error response"
                        }
                    }
                    else -> e.message ?: "Password reset failed"
                }

                Log.e("AuthViewModel", "Password reset failed", e)
                _errorState.value = errorMessage
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

    fun clearPasswordResetStates() {
        Log.d("AuthViewModel", "Clearing password reset states")
        _passwordResetRequestState.value = null
        _otpVerificationState.value = null
        _passwordResetState.value = null
    }

    fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Authenticating with Google ID token")
                val response = googleAuthRepository.authenticateWithGoogle(idToken)
                Log.d("AuthViewModel", "Google authentication successful")

                // Update login state - this will trigger the UI to navigate to the next screen
                _loginState.value = response
                _currentUser.value = response.user
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Google authentication failed", e)
                _errorState.value = e.message ?: "Google authentication failed"
            }
        }
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