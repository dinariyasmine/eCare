package com.example.patientprofile.ui.theme.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.data.repository.UserRepository
import com.example.data.viewModel.UserViewModel
import com.example.patientprofile.ui.theme.components.ProfileHeader
import com.example.patientprofile.ui.theme.components.ProfileOption
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.adamglin.PhosphorIcons
import android.util.Log

import com.adamglin.phosphoricons.regular.Pill
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.CalendarBlank
import com.adamglin.phosphoricons.regular.CarProfile
import com.example.data.viewModel.DoctorViewModel
import com.example.patientprofile.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Doctorparams(
    navController: NavController,
    // Pass the doctor id as a parameter
) {
    val doctorId = 11
    val viewModel: DoctorViewModel = viewModel()
    val doctor by viewModel.selectedDoctor.collectAsState()
    val scrollState = rememberScrollState()

    // Fetch doctor details when doctorId changes

    LaunchedEffect(doctorId) {
        viewModel.loadDoctorDetails(doctorId)
        doctor?.let {
            Log.d("DoctorInfo", "Doctor received: $it")
        }
        Log.d("DoctorInfo", "Doctor received: $doctor")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Doctor Parameters", color = Color.Black) // title text color
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black // icon color
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, // background color
                    titleContentColor = Color.Black, // optional for title
                    navigationIconContentColor = Color.Black // optional for icon
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
                // ProfileHeader(user)
                ProfileHeader(doctor)
                Spacer(modifier = Modifier.height(16.dp))

                ProfileOption(
                    icon = PhosphorIcons.Regular.Pill,
                    text = "Prescriptions"
                ) {
                    //   navController.navigate("prescriptions/${user?.id}")
                }

                ProfileOption(
                    icon = Icons.Default.Person,
                    text = "Personal Information"
                ) {
                    // navController.navigate("personal_info/${user?.id}")
                }
                ProfileOption(
                    icon = PhosphorIcons.Regular.CalendarBlank,
                    text = "Working hours"
                ) {
                    // navController.navigate("working_hours/${user?.id}")
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
                    // simulate logout
                }
            }
        }
    }
}