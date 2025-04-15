package com.example.ecare_mobile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import com.example.data.viewModel.DoctorViewModel
import com.example.doctorlisting.AppNavigation
import com.example.doctorlisting.ui.screen.DoctorListScreen
import java.lang.reflect.Array.set
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

            // ✅ Instantiate the ViewModel with a factory
            val doctorViewModel: DoctorViewModel = viewModel(
                factory = DoctorViewModel.Factory(
                    doctorRepository = DoctorRepository(),
                    userRepository = UserRepository()
                )
            )

            // ✅ Pass ViewModel to your screen
//            DoctorListScreen(
//                navController = navController,
//                doctorViewModel = doctorViewModel
//            )
            val zoneId = ZoneId.of("Africa/Algiers")
            val calendar = Calendar.getInstance(TimeZone.getTimeZone(zoneId))

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            dateFormatter.timeZone = TimeZone.getTimeZone(zoneId)
            val todayDate = Calendar.getInstance(TimeZone.getTimeZone(zoneId)).apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
           // val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
val app=listOf(
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
    Appointment(
        id = 2,
        doctor_id = 102,
        patient_id = 202,
        start_time = dateFormatter.parse("2025-04-15 11:00")!!,
        end_time = dateFormatter.parse("2025-04-15 12:00")!!,
        status = AppointmentStatus.CONFIRMED,
        QR_code = "qr_002",
        date = todayDate
    ),
    Appointment(
        id = 3,
        doctor_id = 103,
        patient_id = 203,
        start_time = dateFormatter.parse("2025-04-15 14:00")!!,
        end_time = dateFormatter.parse("2025-04-15 15:00")!!,
        status = AppointmentStatus.COMPLETED,
        QR_code = "qr_003",
        date = todayDate
    )
)
         com.example.doctorlisting.ui.screen.HomeScreen(navController)
//
           // AppNavigation()
        }
    }
}
