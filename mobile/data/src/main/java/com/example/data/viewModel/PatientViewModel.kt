package com.example.doctorlisting.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Appointment
import com.example.data.model.Patient
import com.example.data.model.Role
import com.example.data.model.User
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.PatientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
class PatientViewModel : ViewModel() {
    private val patientRepository = PatientRepository()  // Replace with actual data fetching logic
    private val appointmentRepository = AppointmentRepository()  // Replace with actual data fetching logic

    var currentPatient by mutableStateOf<Patient?>(  Patient(
        id = 1,
        user_id = 1,
    )
    )
    var appointments by mutableStateOf<List<Appointment>>(emptyList())
    var isLoading by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)

    init {
        loadPatientData()
    }

    private fun loadPatientData() {
        viewModelScope.launch {
            try {
                isLoading = true
                // Fetch logged-in patient data (Replace with logic to get the actual logged-in user)
                val loggedInUser = getLoggedInUser()  // Assume you have a way to get the logged-in user
                val patient = patientRepository.getPatientByUserId(loggedInUser.id)
                currentPatient = patient

                patient?.let { p ->
                    // Fetch appointments for the patient
                    appointments = appointmentRepository.getAppointmentsByPatientId(p.id)
                }

                isLoading = false
            } catch (e: Exception) {
                error = "Failed to load data: ${e.localizedMessage}"
                isLoading = false
            }
        }
    }

    // Mocked method to simulate getting the logged-in user
    private fun getLoggedInUser(): User {
        // Replace with actual logic to get the logged-in user
        return User(id = 1, name = "John Doe", email = "john@example.com", password = "", phone = "", adress = "", role = Role.PATIENT, birth_date = Date())
    }
}
