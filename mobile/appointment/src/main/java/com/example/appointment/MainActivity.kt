package com.example.appointment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.appointment.navigation.AppointmentNavGraph
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
                val navController = rememberNavController()

                AppointmentNavGraph(
                    navController = navController,
                    appointmentViewModel = appointmentModel,
                    availabilityViewModel = availabilityViewModel,
                    startDestination = Screen.ListAppointments.route
                )
            }
        }
    }
}
