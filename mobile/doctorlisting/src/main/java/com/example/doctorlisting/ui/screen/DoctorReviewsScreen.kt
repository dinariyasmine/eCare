package com.example.doctorlisting.ui.screen

import android.annotation.SuppressLint
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.data.model.Doctor
import com.example.data.model.User
import com.example.data.repository.DoctorRepository
import com.example.data.repository.FeedbackRepository
import com.example.data.repository.UserRepository
import com.example.data.viewModel.DoctorViewModel
import com.example.data.viewModel.FeedbackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DoctorReviewsScreen(
    doctorId: Int,
    navController: NavController,
    viewModel: DoctorViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    feedbackViewModel: FeedbackViewModel = viewModel(
        factory = FeedbackViewModel.Factory(FeedbackRepository())
    )
) {
    val doctor by viewModel.selectedDoctor.collectAsState()
    val feedbacks by feedbackViewModel.feedbacks.collectAsState()
    val feedbackLoading by feedbackViewModel.loading.collectAsState()
    val feedbackError by feedbackViewModel.error.collectAsState()

    LaunchedEffect(doctorId) {
        viewModel.loadDoctorDetails(doctorId)
        feedbackViewModel.getFeedbacksByDoctorId(doctorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                backgroundColor = Color(0xFF3B82F6)
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                feedbackLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                feedbackError != null -> {
                    Text(
                        text = feedbackError ?: "An error occurred",
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                feedbacks.isEmpty() -> {
                    Text(
                        text = "No reviews available.",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(feedbacks) { feedback ->
                            FeedbackCard(feedback = feedback)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

