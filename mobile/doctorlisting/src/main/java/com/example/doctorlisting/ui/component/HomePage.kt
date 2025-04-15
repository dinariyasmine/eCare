package com.example.doctorlisting.ui.component
import InfoCardCarousel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.User

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(
    user: User,
    appointments: List<Appointment>,
    unreadNotifications: Int,
    doctors: List<Doctor>,
    navController: NavController,
    users: List<User>
) {
    val scrollState = rememberScrollState()

    // Convert List<User> to Map<Int, User> indexed by user ID
    val usersMap = users.associateBy { it.id }

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
        ScheduleCard(appointments)
        Spacer(modifier = Modifier.height(24.dp))
//        DoctorList(
//            doctors = doctors,
//            users = usersMap,
//            navController = navController,
//            modifier = Modifier.fillMaxSize()
//        )
    }
}