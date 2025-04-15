package com.example.doctorlisting.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.User
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import com.example.doctorlisting.ui.component.HomePage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val doctorRepository = remember { DoctorRepository() }
    val userRepository = remember { UserRepository() }
    val appointmentRepository = remember { AppointmentRepository() }

    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            // Fetch all data in parallel
            val doctorsDeferred = async { doctorRepository.getAllDoctors() }
            val usersDeferred = async { userRepository.getAllUsers() }

            doctors = doctorsDeferred.await()
            users = usersDeferred.await()

            // Get current user (using index 1 as in your original code)
            currentUser = users[0]

            // Fetch appointments if we have a current user
            currentUser?.let { user ->
                appointments = appointmentRepository.getAppointmentsByPatientId(user.id)
            }

            isLoading = false
        }
    }

//    if (isLoading) {
//        // Show loading indicator
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//        return
//    }

//    if (currentUser == null) {
//        // Show error state
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text("No user found.")
//        }
//        return
//    }

    HomePage(

    )
}