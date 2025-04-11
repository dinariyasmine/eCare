package com.example.doctorlisting.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.doctorlisting.data.AppointmentRepository
import com.example.ecare_mobile.data.repository.UserRepository

import com.example.doctorlisting.ui.component.HomePage

@Composable
fun HomeScreen(navController: NavController) {
    val userRepo = UserRepository()
    val appointmentRepo = AppointmentRepository()

    val users = userRepo.getUsers()
    if (users.isEmpty()) {
        // You can show a fallback UI or redirect somewhere else
        Text("No user found.")
        return
    }
//
    val currentUser = users[1]
    val appointments = appointmentRepo.getAppointmentsForUser(currentUser.id)

    HomePage(
        user = currentUser,
        appointments = appointments,
        unreadNotifications = 3
    )
}

