package com.example.doctorlisting.ui.component

import InfoCardCarousel
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.doctorlisting.data.model.Appointment
import com.example.ecare_mobile.data.model.User

@Composable
fun HomePage(user: User, appointments: List<Appointment>, unreadNotifications: Int) {
   Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
 HeaderSection(user, unreadNotifications)
        Spacer(modifier = Modifier.height(16.dp))
        InfoCardCarousel()
        Spacer(modifier = Modifier.height(24.dp))
        Text("Schedule Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        ScheduleTimeline(appointments)
    }
}


