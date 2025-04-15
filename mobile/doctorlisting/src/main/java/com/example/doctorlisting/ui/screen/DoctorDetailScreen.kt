package com.example.doctorlisting.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.data.model.Doctor
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DoctorDetailScreen(doctorId: Int?, navController: NavController) {
    val doctorRepository = remember { DoctorRepository() }
    val userRepository = remember { UserRepository() }
    val doctorState = remember { mutableStateOf<Doctor?>(null) }
    val userState = remember { mutableStateOf<com.example.data.model.User?>(null) }

    LaunchedEffect(doctorId) {
        withContext(Dispatchers.IO) {
            val doctor = doctorId?.let { doctorRepository.getDoctorById(it) }
            doctorState.value = doctor

            doctor?.let {
                userState.value = userRepository.getUserById(it.user_id)
            }
        }
    }

    val doctor = doctorState.value
    val user = userState.value

    if (doctor == null || user == null) {
        Text("Loading...", modifier = Modifier.padding(16.dp))
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Profile Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(doctor.photo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.LightGray, CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = doctor.specialty, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${doctor.grade} out of 5", fontSize = 13.sp)
                    }
                }
            }

            // Social Media Icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val iconBackground = Color(0xFFE0F0FF)
                val iconColor = Color(0xFF3366FF)
                val iconSize = 12.dp
                val circleSize = 22.dp

                listOf(
                    Icons.Default.Star, // Replace with actual social icons if available
                    Icons.Default.Star,
                    Icons.Default.Star,
                    Icons.Default.Star
                ).forEach { icon ->
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .clip(CircleShape)
                            .background(iconBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Section
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = "Patients", fontSize = 13.sp, color = Color.Gray)
                Text(text = doctor.nbr_patients.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(text = "Specialty", fontSize = 13.sp, color = Color.Gray)
                Text(text = doctor.specialty, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3366FF))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(text = "About", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = doctor.description, fontSize = 13.sp, color = Color.DarkGray)

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews Section
        Text(text = "Rating", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "★ ${doctor.grade} out of 5", fontSize = 14.sp, color = Color.Gray)

        Button(
            onClick = { navController.navigate("doctor/${doctor.id}/reviews") },
            modifier = Modifier.align(Alignment.End).padding(vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3B82F6),
                contentColor = Color.White
            )
        ) {
            Text("See all")
        }

        // Since reviews aren't in the model, we'll skip displaying them
        // You would need to add reviews to the Doctor model or fetch them separately

        Spacer(modifier = Modifier.height(16.dp))

        // Location
        Text(text = "Location", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color.LightGray, RoundedCornerShape(12.dp))
        ) {
            Text(
                text = "Map Placeholder",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Book Button
        Button(
            onClick = { /* Navigate to booking */ },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3366FF)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Book an Appointment", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}