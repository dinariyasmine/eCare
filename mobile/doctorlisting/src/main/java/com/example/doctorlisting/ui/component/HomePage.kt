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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.viewModel.DoctorViewModel
import com.example.doctorlisting.ui.screen.DoctorCard
import com.example.data.viewModel.HomeViewModel
import com.example.data.viewModel.HomeViewState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController? = null, // Optional if you need navigation
    homeViewModel: HomeViewModel = viewModel(), // Inject HomeViewModel
    doctorViewModel: DoctorViewModel = viewModel() // Inject the existing DoctorViewModel
) {
    // Observe ViewModels' state
    val homeViewState by homeViewModel.state.collectAsState()

    // Observe doctor-related states from DoctorViewModel
    val doctors by doctorViewModel.doctors.collectAsState()
    val isLoading by doctorViewModel.loading.collectAsState()
    val error by doctorViewModel.error.collectAsState()

    // Fetch doctors when composable is first created
    LaunchedEffect(Unit) {
        doctorViewModel.fetchAllDoctors()
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
                    text = error ?: "Unknown error",
                    modifier = Modifier.padding(16.dp)
                )
            }
            homeViewState is HomeViewState.Success -> {
                val data = (homeViewState as HomeViewState.Success).data

                // Main content when data is loaded
                if (data.currentUser != null) {
                    HeaderSection(user = data.currentUser, unreadNotifications = 1)

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoCardCarousel()
                    Spacer(Modifier.height(24.dp))

                    Text("Schedule Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    ScheduleCard(data.appointments)
                    Spacer(Modifier.height(24.dp))

                    // Display doctors from DoctorViewModel instead
                    Text("Available Doctors", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))

                    // Check if there are doctors before accessing the list
                    if (doctors.isNotEmpty() && navController != null) {
                        // Iterate through the list and pass each doctor to DoctorCard
                        doctors.forEach { doctor ->
                            DoctorCard(doctor = doctor, navController = navController)
                        }
                    } else {
                        Text("No doctors available")
                    }
                } else {
                    Text("No user data available")
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}