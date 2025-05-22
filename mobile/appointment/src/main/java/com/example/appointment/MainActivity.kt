package com.example.appointment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.appointment.navigation.AppointmentNavGraph
import com.example.appointment.navigation.BottomNavBar
import com.example.appointment.navigation.Screen
import com.example.core.theme.ECareMobileTheme
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECareMobileTheme {
                val app = applicationContext as MyApplication
                val appointmentModel = AppointmentViewModel(app.appointmentRepository)
                val availabilityViewModel = AvailabilityViewModel(app.availabilityRepository, 11)
                MainAppScreen(
                    userRole = "doctor",
                    appointmentViewModel = appointmentModel,
                    availabilityViewModel = availabilityViewModel
                )
            }
        }
    }
}

@Composable
fun MainAppScreen(
    userRole: String, // "patient" or "doctor"
    appointmentViewModel: AppointmentViewModel,
    availabilityViewModel: AvailabilityViewModel
) {
    val navController = rememberNavController()
    var selectedTabIndex by remember { mutableStateOf(1) } // Default to second tab (Medical/Appointments)

    // Set the start destination based on role
    val startDestination = if (userRole == "doctor") Screen.DoctorAppointments.route else Screen.ListAppointments.route

    // Map tabs to destinations
    val tabDestinations = listOf(
        "home",
        if (userRole == "doctor") Screen.DoctorAppointments.route else Screen.ListAppointments.route,
        if (userRole == "doctor") Screen.DoctorAvailabilities.route else Screen.NewAppointment.route,
        "notifications",
        "profile"
    )

    // Handle tab changes
    LaunchedEffect(selectedTabIndex) {
        val destination = tabDestinations[selectedTabIndex]
        // Only navigate if we're not already at this destination
        if (navController.currentDestination?.route != destination) {
            navController.navigate(destination) {
                // Clear back stack for root destinations
                if (selectedTabIndex in listOf(0, 1, 2, 3, 4)) {
                    popUpTo(startDestination) { saveState = true }
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItemIndex = selectedTabIndex,
                onItemSelected = { newIndex ->
                    selectedTabIndex = newIndex
                }
            )
        }
    ) { innerPadding ->
        AppointmentNavGraph(
            navController = navController,
            appointmentViewModel = appointmentViewModel,
            availabilityViewModel = availabilityViewModel,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}