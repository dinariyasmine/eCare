package com.example.doctorlisting.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.model.Doctor
import com.example.data.model.User
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DoctorReviewsScreen(doctorId: Int?, navController: NavController) {
    val doctorRepository = remember { DoctorRepository() }
    val userRepository = remember { UserRepository() }
    val doctorState = remember { mutableStateOf<Doctor?>(null) }
    val userState = remember { mutableStateOf<User?>(null) }

    // Since reviews aren't part of the Doctor model in your provided code,
    // I'll create a simple mock review data class
    data class Review(
        val author: String,
        val text: String,
        val rating: Float
    )

    // Mock reviews - in a real app, you'd fetch these from a repository
    val mockReviews = remember {
        listOf(
            Review("Sarah Johnson", "Dr. Smith was very professional and helpful.", 4.5f),
            Review("Michael Brown", "Excellent consultation, would recommend.", 5f),
            Review("Emily Davis", "Very knowledgeable and patient.", 4f)
        )
    }

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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${user.name}'s Reviews") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Average rating section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Average Rating: ${doctor.grade}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reviews list
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(mockReviews) { review ->
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
                                // Display star rating
                                Row {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Rating",
                                            tint = if (index < review.rating.toInt())
                                                Color(0xFFFFC107) else Color.LightGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = review.text, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}