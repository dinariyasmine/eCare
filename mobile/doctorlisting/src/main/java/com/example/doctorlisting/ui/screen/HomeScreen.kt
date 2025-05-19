package com.example.doctorlisting.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.data.model.Appointment
import com.example.data.model.Patient
import com.example.data.model.User
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.UserRepository
import com.example.doctorlisting.ui.component.HomePage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val userRepository = remember { UserRepository() }
    val appointmentRepository = remember { AppointmentRepository() }

    // States
    var currentUser by remember { mutableStateOf<User?>(null) }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect for side-effect to fetch data
    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                // Fetch users in parallel
                val usersDeferred = async { userRepository.getAllUsers() }
                val users = usersDeferred.await()

                // Get the first patient (you can adjust this logic as per your app's flow)
                currentUser = users.firstOrNull { it.role == com.example.data.model.Role.PATIENT }

                currentUser?.let { user ->
                    // Fetch appointments for the current patient
                    appointments = appointmentRepository.getAppointmentsByPatientId(user.id)
                }
            }
        } catch (e: Exception) {
            error = "Failed to load data: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    // UI for loading, error, or main content
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error ?: "An error occurred.")
        }
        return
    }

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No user found.")
        }
        return
    }

    // Show the HomePage with the current user and appointments
    HomePage(modifier = Modifier.fillMaxSize(), navController = navController)
}
