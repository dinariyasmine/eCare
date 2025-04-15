package com.example.doctorlisting.ui.component

import InfoCardCarousel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.User
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController? = null // Optional if you need navigation
) {
    // Repositories
    val doctorRepository = remember { DoctorRepository() }
    val userRepository = remember { UserRepository() }
    val appointmentRepository = remember { AppointmentRepository() }

    // States
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch data
    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                // Parallel data fetching
                val (fetchedDoctors, fetchedUsers) = Pair(
                    async { doctorRepository.getAllDoctors() }.await(),
                    async { userRepository.getAllUsers() }.await()
                )

                doctors = fetchedDoctors
                users = fetchedUsers
                currentUser = fetchedUsers.firstOrNull() // Safely get first user or null

                // Fetch appointments if user exists

                    appointments = appointmentRepository.getAppointmentsByPatientId(1)

            }
        } catch (e: Exception) {
            error = "Failed to load data: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    // UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            error != null -> {
                Text(
                    text = error!!,
                    modifier = Modifier.padding(16.dp)
                )
            }
            currentUser == null -> {
                Text("No user data available")
            }
            else -> {
                // Main content when data is loaded
                HeaderSection(user = currentUser!!, unreadNotifications = 1)

                Spacer(modifier = Modifier.height(16.dp))

                // Uncomment these when ready

                InfoCardCarousel()
                Spacer(Modifier.height(24.dp))
                Text("Schedule Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                ScheduleCard(appointments)
                Spacer(Modifier.height(24.dp))
               /* DoctorList(
                    doctors = doctors,
                    users = users.associateBy { it.id },
                    navController = navController
                )
                */
            }
        }
    }
}