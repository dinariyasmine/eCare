package com.example.doctorlisting.ui.screen

import androidx.compose.material.icons.filled.ArrowBack


import com.adamglin.PhosphorIcons
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
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
fun DoctorReviewsScreen(doctorId: Int?, navController: NavController) {
    val doctorRepository = remember { DoctorsRepositoryImpl() }
    val doctor = remember(doctorId) { doctorRepository.getDoctorById(doctorId ?: -1) }

    if (doctor == null) {
        Text("Doctor not found", modifier = Modifier.padding(16.dp))
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${doctor.name}'s Reviews") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White

                        )
                    }
                }
            )

        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Average Rating: ${doctor.rating}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show all reviews
            doctor.reviews.forEach { review ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color(0xFFF5F5F5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = review.author,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = PhosphorIcons.Regular.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                           // Text(text = review.rating.toString())
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = review.text, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}