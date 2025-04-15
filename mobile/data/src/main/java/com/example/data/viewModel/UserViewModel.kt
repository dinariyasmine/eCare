package com.example.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Role
import com.example.data.model.User
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> get() = _selectedUser

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val sdf = SimpleDateFormat("yyyy-MM-dd")

    init {
        fetchAllUsers()
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _users.value = userRepository.getAllUsers()
            } catch (e: Exception) {
                _error.value = "Failed to load users: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getUserById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val user = userRepository.getUserById(id)
                if (user != null) {
                    _selectedUser.value = user
                } else {
                    _error.value = "User not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get user: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getUserByEmail(email: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    _selectedUser.value = user
                    _users.value = listOf(user)
                } else {
                    _error.value = "User not found with this email"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get user by email: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getUsersByRole(role: Role) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _users.value = userRepository.getUsersByRole(role)
            } catch (e: Exception) {
                _error.value = "Failed to load users by role: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createUser(
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String,
        role: Role,
        birthDateString: String
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

                if (email.isBlank()) {
                    _error.value = "Email cannot be empty"
                    _loading.value = false
                    return@launch
                }

                if (password.isBlank()) {
                    _error.value = "Password cannot be empty"
                    _loading.value = false
                    return@launch
                }

                // Parse birth date
                val birthDate = if (birthDateString.isNotBlank()) {
                    try {
                        sdf.parse(birthDateString)
                    } catch (e: Exception) {
                        _error.value = "Invalid birth date format. Use yyyy-MM-dd"
                        _loading.value = false
                        return@launch
                    }
                } else {
                    null
                }

                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_users.value.maxOfOrNull { it.id } ?: 0) + 1

                val newUser = birthDate?.let {
                    User(
                        id = newId,
                        name = name,
                        email = email,
                        password = password,
                        phone = phone,
                        adress = address,
                        role = role,
                        birth_date = it
                    )
                }

                val success = newUser?.let { userRepository.createUser(it) }
                if (success == true) {
                    fetchAllUsers() // Refresh the list
                } else {
                    _error.value = "Failed to create user: User with the same ID or email already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create user: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateUser(
        id: Int,
        name: String,
        email: String,
        password: String,
        phone: String,
        address: String,
        role: Role,
        birthDateString: String
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

                if (email.isBlank()) {
                    _error.value = "Email cannot be empty"
                    _loading.value = false
                    return@launch
                }

                if (password.isBlank()) {
                    _error.value = "Password cannot be empty"
                    _loading.value = false
                    return@launch
                }

                // Parse birth date
                val birthDate = if (birthDateString.isNotBlank()) {
                    try {
                        sdf.parse(birthDateString)
                    } catch (e: Exception) {
                        _error.value = "Invalid birth date format. Use yyyy-MM-dd"
                        _loading.value = false
                        return@launch
                    }
                } else {
                    null
                }

                val updatedUser = birthDate?.let {
                    User(
                        id = id,
                        name = name,
                        email = email,
                        password = password,
                        phone = phone,
                        adress = address,
                        role = role,
                        birth_date = it
                    )
                }

                val success = updatedUser?.let { userRepository.updateUser(it) }
                if (success == true) {
                    fetchAllUsers() // Refresh the list
                    if (_selectedUser.value?.id == id) {
                        _selectedUser.value = updatedUser // Update selected user if it was selected
                    }
                } else {
                    _error.value = "Failed to update user: User not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update user: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = userRepository.deleteUser(id)
                if (success) {
                    fetchAllUsers() // Refresh the list
                    if (_selectedUser.value?.id == id) {
                        _selectedUser.value = null // Clear selected user if it was deleted
                    }
                } else {
                    _error.value = "Failed to delete user: User not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete user: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchUsersByName(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _users.value = userRepository.searchUsersByName(query)
            } catch (e: Exception) {
                _error.value = "Failed to search users by name: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun formatDate(date: Date?): String {
        return userRepository.formatDate(date)
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedUser() {
        _selectedUser.value = null
    }

    // Factory class to provide UserRepository dependency
    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                return UserViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
