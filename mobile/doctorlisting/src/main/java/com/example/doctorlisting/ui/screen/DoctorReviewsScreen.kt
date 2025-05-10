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
import androidx.navigation.NavController
import com.example.data.model.Doctor
import com.example.data.model.User
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import com.example.data.viewModel.DoctorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DoctorReviewsScreen(
    doctorId: Int,
    navController: NavController,
    viewModel: DoctorViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val doctor by viewModel.doctorDetails.collectAsState()
    val feedbacks by viewModel.doctorDetails.collectAsState()

    LaunchedEffect(doctorId) {
        viewModel.loadDoctorDetails(doctorId)
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
        if (feedbacks?.feedbacks?.isNotEmpty() == true) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(feedbacks?.feedbacks.orEmpty()) { feedback ->
                    FeedbackCard(feedback = feedback)
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No reviews available.")
            }
        }
    }
}

