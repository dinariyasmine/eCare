
package com.example.doctorlisting.ui.component
import InfoCardCarousel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doctorlisting.data.model.Appointment
import com.example.doctorlisting.data.model.Doctor
import com.example.doctorlisting.ui.component.DoctorList
import com.example.doctorlisting.ui.component.HeaderSection
import com.example.doctorlisting.ui.component.ScheduleTimeline
import com.example.doctorlisting.ui.screen.DoctorCard
import com.example.ecare_mobile.data.model.User

@Composable
fun HomePage(
    user: User,
    appointments: List<Appointment>,
    unreadNotifications: Int,
    doctors: List<Doctor>,
    navController: NavController
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        HeaderSection(user, unreadNotifications)
        Spacer(modifier = Modifier.height(16.dp))
        InfoCardCarousel()
        Spacer(modifier = Modifier.height(24.dp))
        Text("Schedule Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        ScheduleTimeline(appointments)
        Spacer(modifier = Modifier.height(24.dp))
        DoctorList(
            doctors = doctors,
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )


    }
}
