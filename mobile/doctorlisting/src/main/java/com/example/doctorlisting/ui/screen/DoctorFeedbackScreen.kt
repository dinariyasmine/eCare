package com.example.doctorlisting.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.network.SubmitFeedbackRequest
import com.example.data.repository.FeedbackRepository
import com.example.data.viewModel.DoctorViewModel
import com.example.data.viewModel.FeedbackViewModel

@Composable
fun DoctorFeedbackScreen(
    doctorId: Int?,
    navController: NavController,
    viewModel: DoctorViewModel = viewModel(),
    feedbackViewModel: FeedbackViewModel = viewModel(
        factory = FeedbackViewModel.Factory(FeedbackRepository())
    )
) {
    val doctor by viewModel.selectedDoctor.collectAsState()
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(doctorId) {
        doctorId?.let {
            viewModel.loadDoctorDetails(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Text(
                text = "Feedback your Doctor",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Doctor Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                doctor?.name?.let {
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = doctor?.specialty ?: "Pediatrician",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star ${index + 1}",
                            tint = if (index < rating) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { rating = index + 1 }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Review",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            placeholder = { Text("Write your review") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.LightGray,
                unfocusedIndicatorColor = Color.LightGray,
                cursorColor = Color(0xFF3B82F6)
            ),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                doctorId?.let {
                    val feedbackRequest = SubmitFeedbackRequest(
                        patient_id = 9,
                        title = "Rating: $rating/5",  // Create a title based on rating
                        description = reviewText      // Use reviewText as description
                    )

                    Log.d("DoctorFeedbackScreen", "Submitting feedback: $feedbackRequest")
                    feedbackViewModel.submitFeedback(it, feedbackRequest) { success ->
                        if (success) {
                            Toast
                                .makeText(context, "Feedback submitted successfully!", Toast.LENGTH_SHORT)
                                .show()
                            reviewText = ""
                            rating = 0
                            navController.popBackStack()
                        } else {
                            Toast
                                .makeText(context, "Failed to submit feedback", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3B82F6))
        ) {
            Text(
                text = "Leave a Review",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}