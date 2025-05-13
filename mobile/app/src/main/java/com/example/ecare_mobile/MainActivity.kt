package com.example.ecare_mobile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import com.example.data.viewModel.DoctorViewModel
import com.example.doctorlisting.ui.screen.DoctorDetailScreen
//import com.example.doctorlisting.ui.screen.DoctorDetailScreen
import com.example.doctorlisting.ui.screen.DoctorListScreen
import com.example.doctorlisting.ui.screen.DoctorReviewsScreen
//import com.example.doctorlisting.ui.screen.DoctorReviewsScreen
import com.example.doctorlisting.ui.screen.HomeScreen
import com.example.patientprofile.ui.theme.screens.Doctorparams
import com.example.patientprofile.ui.theme.screens.PatientProfileScreen
import com.example.patientprofile.ui.theme.screens.Patientparams

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val doctorViewModel: DoctorViewModel = viewModel()

            // Set up date formatting for sample data
            val zoneId = ZoneId.of("Africa/Algiers")
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            dateFormatter.timeZone = TimeZone.getTimeZone(zoneId)
            val todayDate = Calendar.getInstance(TimeZone.getTimeZone(zoneId)).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            // Sample appointment data
            val appointments = listOf(
                Appointment(
                    id = 1,
                    doctor_id = 101,
                    patient_id = 201,
                    start_time = dateFormatter.parse("2025-04-15 09:00")!!,
                    end_time = dateFormatter.parse("2025-04-15 10:00")!!,
                    status = AppointmentStatus.IN_PROGRESS,
                    QR_code = "qr_001",
                    date = todayDate
                ),
                // Other appointments...
            )

            val userRepository = UserRepository()
            val doctorRepository = DoctorRepository()

            // Configure NavHost with proper routes
            NavHost(navController = navController, startDestination = "Patientparams") {
                // Add the missing doctor_list composable route
                composable("doctor_list") {
                    DoctorListScreen(
                        navController = navController,
                    )
                }
                composable("home") {
                    HomeScreen(navController = navController)
                }

                // Doctor detail screen
                composable("doctor/{doctorId}") { backStackEntry ->
                    val doctorId = backStackEntry.arguments?.getString("doctorId")?.toInt()
                    DoctorDetailScreen(doctorId = doctorId, navController = navController)
                }
                composable("doctor/{doctorId}/reviews") { backStackEntry ->
                    val doctorId = backStackEntry.arguments?.getString("doctorId")?.toIntOrNull()
                    if (doctorId != null) {
                        DoctorReviewsScreen(doctorId = doctorId, navController = navController)
                    }
                }



                composable("Docotrparams") {
                    Doctorparams(
                        navController = navController

                    )
                }
                composable("patient_profile/{patientId}") { backStackEntry ->
                    val patientId = backStackEntry.arguments?.getString("patientId")?.toIntOrNull()
                    if (patientId != null) {
                        PatientProfileScreen(patientId = patientId)
                    }
                }


                composable("Patientparams") {
                    Patientparams(
                        navController = navController

                    )
                }

                // Add any other screens your app needs
            }
        }
    }
}

