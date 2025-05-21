package com.example.doctorlisting.ui.component
import InfoCardCarousel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.viewModel.DoctorViewModel
import com.example.data.viewModel.HomeViewModel
import com.example.data.viewModel.HomeViewState
import com.example.doctorlisting.ui.screen.DoctorCard

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
        doctorViewModel.getDoctorsFromApi()
       // Log.d("HomePage", "Doctors list: $doctors")
    }
    LaunchedEffect(doctors) {
        Log.d("HomePage", "Doctors list updated: $doctors")
    }
    // UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(10.dp)
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
                    modifier = Modifier.padding(10.dp)
                )
            }
            homeViewState is HomeViewState.Success -> {
                val data = (homeViewState as HomeViewState.Success).data

                // Main content when data is loaded
                if (data.currentUser != null) {
                    HeaderSection(user = data.currentUser, unreadNotifications = 1)

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoCardCarousel()
                    Spacer(Modifier.height(24.dp).padding(16.dp))

                  //  Text("Schedule Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    ScheduleCard(data.appointments)
                    Spacer(Modifier.height(24.dp))

                    // Display doctors from DoctorViewModel instead
                   // Text("Available Doctors", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Doctors",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        TextButton(
                            onClick = {
                                navController?.navigate("doctor_list")
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color.Gray,
                                backgroundColor = Color.Transparent
                            )
                        ) {
                            Text("See All")
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    // Check if there are doctors before accessing the list
                    if (doctors.isNotEmpty() && navController != null) {
                        // Iterate through the list and pass each doctor to DoctorCard

                           DoctorCard(doctor = doctors[1], navController = navController)

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