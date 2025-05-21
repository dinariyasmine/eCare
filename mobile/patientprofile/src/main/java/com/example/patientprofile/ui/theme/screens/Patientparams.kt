package com.example.patientprofile.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Pill
import com.example.data.network.TokenManager
import com.example.data.viewModel.PatientViewModel
import com.example.patientprofile.ui.theme.components.ProfileHeader
import com.example.patientprofile.ui.theme.components.ProfileOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Patientparams(
    navController: NavController,
    // Replace or pass this dynamically
) {
    val patientId = com.example.data.util.TokenManager.getUserId()
    val viewModel: PatientViewModel = viewModel()
    val patient by viewModel.selectedPatient.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(patientId) {
        viewModel.loadPatientDetails(patientId)
        Log.d("PatientInfo", "Patient loaded: $patient")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {   },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ProfileHeader(patient)

                Spacer(modifier = Modifier.height(16.dp))

                ProfileOption(
                    icon = PhosphorIcons.Regular.Pill,
                    text = "Prescriptions",

                ) {

                }

                ProfileOption(
                    icon = Icons.Default.Person,
                    text = "Personal Information"
                ) {
                    // navController.navigate("personal_info/${patient?.id}")
                    // Replace with actual patientId
                    navController.navigate("patient_profile/$patientId")
                }

                ProfileOption(
                    icon = Icons.Default.Lock,
                    text = "Reset password"
                ) {
                    navController.navigate("reset_password")
                }

                ProfileOption(
                    icon = Icons.Default.ExitToApp,
                    text = "Log out"
                ) {
                    // Handle logout
                }
            }
        }
    }
}
