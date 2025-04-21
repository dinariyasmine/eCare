package com.example.doctorlisting.ui.component

import InfoCardCarousel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.MapPin
import com.adamglin.phosphoricons.regular.Star
import com.example.data.model.Appointment
import com.example.data.model.Doctor
import com.example.data.model.User
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController? = null // Optional if you need navigation
) {
    // Repositories
    val doctorRepository = remember { DoctorRepository() }
    val userRepository = remember { UserRepository() }
    val appointmentRepository = remember { AppointmentRepository() }

    // States
    var doctors by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Fetch data
    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                // Parallel data fetching
                val (fetchedDoctors, fetchedUsers) = Pair(
                    async { doctorRepository.getAllDoctors() }.await(),
                    async { userRepository.getAllUsers() }.await()
                )

                doctors = fetchedDoctors
                users = fetchedUsers
                currentUser = fetchedUsers.firstOrNull() // Safely get first user or null

                // Fetch appointments if user exists

                    appointments = appointmentRepository.getAppointmentsByPatientId(1)

            }
        } catch (e: Exception) {
            error = "Failed to load data: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    // UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            error != null -> {
                Text(
                    text = error!!,
                    modifier = Modifier.padding(16.dp)
                )
            }
            currentUser == null -> {
                Text("No user data available")
            }
            else -> {
                // Main content when data is loaded
                HeaderSection(user = currentUser!!, unreadNotifications = 1)

                Spacer(modifier = Modifier.height(16.dp))

                // Uncomment these when ready

                InfoCardCarousel()
                Spacer(Modifier.height(24.dp))
                Text("Schedule Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                ScheduleCard(appointments)
                Spacer(Modifier.height(24.dp))
                val dd=Doctor(
                    id = 8,
                    user_id = 108,
                    photo = "https://example.com/doctors/8.jpg",
                    specialty = "Psychiatry",
                    clinic_id = 6,
                    grade = 4.4f,
                    description = "Mental health professional with therapy focus",
                    nbr_patients = 765
                )
                if (navController != null) {
                    DoctorCard(dd,  navController)
                }
               /* DoctorList(
                    doctors = doctors,
                    users = users.associateBy { it.id },
                    navController = navController
                )
                */
            }
        }
    }
}
@Composable
fun DoctorCard(doctor: Doctor, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { navController.navigate("doctor/${doctor.id}") },
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Image du docteur
            Image(
                painter = rememberImagePainter(data = doctor.photo), // Utilisation de doctor.photo [4, 5]
                contentDescription = "Doctor Image",
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Infos du docteur
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material.Text(text = doctor.specialty, fontWeight = FontWeight.Bold, fontSize = 18.sp) // Affichage de la spécialité [4, 5]
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = PhosphorIcons.Regular.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    androidx.compose.material.Text(text = doctor.grade.toString(), fontSize = 14.sp) // Affichage de la note [4, 5]
                }
                androidx.compose.material.Text(text = doctor.specialty, color = Color.Gray, fontSize = 14.sp) // Redondant, déjà affiché au-dessus
                androidx.compose.material.Text(
                    text = doctor.description, // Utilisation de la description du docteur [4, 5]
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2 // Limiter le nombre de lignes pour la description
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = PhosphorIcons.Regular.MapPin,
                        contentDescription = "Location",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    androidx.compose.material.Text(text = "2.4 km away", fontSize = 12.sp, color = Color.Gray) // La distance n'est pas dans le modèle
                }
            }
        }
    }
}