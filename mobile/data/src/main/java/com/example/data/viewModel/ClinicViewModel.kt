package com.example.data.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Clinic
import com.example.data.repository.ClinicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClinicViewModel(private val clinicRepository: ClinicRepository) : ViewModel() {

    private val _clinics = MutableStateFlow<List<Clinic>>(emptyList())
    val clinics: StateFlow<List<Clinic>> get() = _clinics

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    init {
        fetchAllClinics()
    }

    fun fetchAllClinics() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val clinicList = clinicRepository.getAllClinics()
                Log.d("ClinicViewModel", "Fetched ${clinicList.size} clinics")

                if (clinicList.isEmpty()) {
                    Log.w("ClinicViewModel", "Clinic list is empty from repository")
                } else {
                    Log.d("ClinicViewModel", "First clinic: ${clinicList.first().name}")
                }

                _clinics.value = clinicList
            } catch (e: Exception) {
                Log.e("ClinicViewModel", "Failed to load clinics", e)
                _error.value = "Failed to load clinics: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }


    fun getClinicById(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val clinic = clinicRepository.getClinicById(id)
                if (clinic != null) {
                    _clinics.value = listOf(clinic)
                } else {
                    _error.value = "Clinic not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to get clinic: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun createClinic(name: String, address: String, mapLocation: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                // Generate a new ID (in a real app, this would be handled by the backend)
                val newId = (_clinics.value.maxOfOrNull { it.id } ?: 0) + 1

                val newClinic = Clinic(
                    id = newId,
                    name = name,
                    adress = address,
                    map_location = mapLocation
                )

                val success = clinicRepository.createClinic(newClinic)
                if (success) {
                    fetchAllClinics() // Refresh the list
                } else {
                    _error.value = "Failed to create clinic: Clinic with the same ID already exists"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create clinic: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateClinic(clinic: Clinic) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = clinicRepository.updateClinic(clinic)
                if (success) {
                    fetchAllClinics() // Refresh the list
                } else {
                    _error.value = "Failed to update clinic: Clinic not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update clinic: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteClinic(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val success = clinicRepository.deleteClinic(id)
                if (success) {
                    fetchAllClinics() // Refresh the list
                } else {
                    _error.value = "Failed to delete clinic: Clinic not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete clinic: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchClinicsByName(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _clinics.value = clinicRepository.searchClinicsByName(query)
            } catch (e: Exception) {
                _error.value = "Failed to search clinics by name: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun searchClinicsByAddress(query: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _clinics.value = clinicRepository.searchClinicsByAddress(query)
            } catch (e: Exception) {
                _error.value = "Failed to search clinics by address: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Factory class to provide ClinicRepository dependency
    class Factory(private val clinicRepository: ClinicRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClinicViewModel::class.java)) {
                return ClinicViewModel(clinicRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
