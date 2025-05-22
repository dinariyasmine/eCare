package com.example.appointment.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.appointment.ui.screen.doctor.ListAppointmentsDoctorScreen
import com.example.appointment.ui.screen.doctor.ListAvailabilitiesScreen
import com.example.appointment.ui.screen.doctor.ViewCompletedAppointmentDoctorScreen
import com.example.appointment.ui.screen.doctor.ViewConfirmedAppointmentDoctorScreen
import com.example.appointment.ui.screen.patient.ListAppointmentsScreen
import com.example.appointment.ui.screen.patient.NewAppointmentScreen
import com.example.appointment.ui.screen.patient.RescheduleAppointmentScreen
//import com.example.appointment.ui.screen.patient.RescheduleAppointmentScreen
import com.example.appointment.ui.screen.patient.ViewConfirmedAppointmentScreen
import com.example.appointment.ui.screen.patient.ViewCompletedAppointmentScreen
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    object DoctorAppointments : Screen("doctor/appointments")
    object DoctorAvailabilities : Screen("doctor/availabilities")
    object DoctorConfirmedAppointment : Screen("doctor/view-confirmed/{appointmentId}") {
        fun createRoute(appointmentId: String) = "doctor/view-confirmed/$appointmentId"
    }
    object DoctorCompletedAppointment : Screen("doctor/view-completed/{appointmentId}") {
        fun createRoute(appointmentId: String) = "doctor/view-completed/$appointmentId"
    }
    object NewAppointment : Screen("patient/new-appointment")
    object ListAppointments : Screen("patient/appointments")
    object RescheduleAppointment : Screen("patient/reschedule/{appointmentId}") {
        fun createRoute(appointmentId: String) = "patient/reschedule/$appointmentId"
    }
    object ViewConfirmedAppointment : Screen("patient/view-confirmed/{appointmentId}") {
        fun createRoute(appointmentId: String) = "patient/view-confirmed/$appointmentId"
    }
    object ViewCompletedAppointment : Screen("patient/view-completed/{appointmentId}") {
        fun createRoute(appointmentId: String) = "patient/view-completed/$appointmentId"
    }
}

@Composable
fun AppointmentNavGraph(
    navController: NavHostController,
    appointmentViewModel: AppointmentViewModel,
    availabilityViewModel: AvailabilityViewModel,
    startDestination: String = Screen.DoctorAppointments.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.DoctorAppointments.route) {
            ListAppointmentsDoctorScreen(appointmentViewModel, navController)
        }

        composable(Screen.DoctorAvailabilities.route) {
            ListAvailabilitiesScreen(availabilityViewModel = availabilityViewModel)
        }

        composable(Screen.DoctorCompletedAppointment.route) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")?.toIntOrNull()
            if (appointmentId != null) {
                ViewCompletedAppointmentDoctorScreen(
                    appointmentViewModel,
                    availabilityViewModel,
                    navController,
                    appointmentId
                )
            }
        }

        composable(Screen.DoctorConfirmedAppointment.route) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")?.toIntOrNull()
            if (appointmentId != null) {
                ViewConfirmedAppointmentDoctorScreen(
                    appointmentViewModel,
                    availabilityViewModel,
                    navController,
                    appointmentId
                )
            }
        }

        composable(Screen.NewAppointment.route) {
            NewAppointmentScreen(
                viewModel = appointmentViewModel,
                availabilityViewModel = availabilityViewModel
            )
        }

        composable(Screen.ListAppointments.route) {
            ListAppointmentsScreen(
                viewModel = appointmentViewModel,
                navController = navController
            )
        }

        composable(Screen.ViewCompletedAppointment.route) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")?.toIntOrNull()
            if (appointmentId != null) {
                ViewCompletedAppointmentScreen(
                    appointmentViewModel,
                    availabilityViewModel,
                    navController,
                    appointmentId
                )
            }
        }

        composable(Screen.ViewConfirmedAppointment.route) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")?.toIntOrNull()
            if (appointmentId != null) {
                ViewConfirmedAppointmentScreen(
                    appointmentViewModel,
                    availabilityViewModel,
                    navController,
                    appointmentId
                )
            }
        }

        composable(Screen.RescheduleAppointment.route) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId")
            if (appointmentId != null) {
                RescheduleAppointmentScreen(
                    appointmentViewModel,
                    availabilityViewModel,
                    navController,
                    appointmentId
                )
            }
        }
    }
}