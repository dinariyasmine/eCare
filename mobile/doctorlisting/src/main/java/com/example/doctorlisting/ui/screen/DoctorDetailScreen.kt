package com.example.doctorlisting.ui.screen
import com.adamglin.PhosphorIcons
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
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
import com.example.doctorlisting.data.repository.DoctorsRepositoryImpl

import com.adamglin.phosphoricons.regular.InstagramLogo
import com.adamglin.phosphoricons.regular.LinkedinLogo
import com.adamglin.phosphoricons.regular.Phone
import com.adamglin.phosphoricons.regular.Envelope

import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Star

@Composable
fun DoctorDetailScreen(doctorId: Int?, navController: NavController) {
    val doctorRepository = remember { DoctorsRepositoryImpl() }
    val doctor = remember(doctorId) { doctorRepository.getDoctorById(doctorId ?: -1) }

    if (doctor == null) {
        Text("Doctor not found", modifier = Modifier.padding(16.dp))
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
                    painter = rememberImagePainter(doctor.image),
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
                    Text(text = doctor.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = doctor.specialty, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${doctor.rating} out of 5", fontSize = 13.sp)
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
                    PhosphorIcons.Regular.InstagramLogo,
                    PhosphorIcons.Regular.LinkedinLogo,
                    PhosphorIcons.Regular.Phone,
                    PhosphorIcons.Regular.Envelope
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

        // Payment
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = "Patients", fontSize = 13.sp, color = Color.Gray)
                Text(text = doctor.patients.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(text = "Payment", fontSize = 13.sp, color = Color.Gray)
                Text(text = "${doctor.payment} DZD", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3366FF))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Education
        Text(text = "Education", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = doctor.education, fontSize = 13.sp, color = Color.DarkGray)

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews Section
        Text(text = "Rating", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "★ ${doctor.rating} out of 5", fontSize = 14.sp, color = Color.Gray)

        Button(
            onClick = {navController.navigate("doctor/${doctor.id}/reviews")  },
            modifier = Modifier.align(Alignment.End).padding(vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3B82F6), // Button background color
                contentColor = Color.White // Text and icon color
            )

        ) {
            Text("See all")
        }

        doctor.reviews.take(2).forEach { review ->
            Card(
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color(0xFFF5F5F5),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = review.author, fontWeight = FontWeight.Bold)
                    Text(text = review.text, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Location
        Text(text = "Location", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        // Replace with actual map if available
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
