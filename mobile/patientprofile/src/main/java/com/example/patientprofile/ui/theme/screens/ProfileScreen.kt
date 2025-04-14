package com.example.patientprofile.ui.theme.screens

 
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.data.repository.UserRepository
import com.example.data.viewModel.UserViewModel
import com.example.patientprofile.ui.theme.components.ProfileHeader
import com.example.patientprofile.ui.theme.components.ProfileOption
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamglin.PhosphorIcons

import com.adamglin.phosphoricons.regular.Pill
import com.adamglin.phosphoricons.Regular


@Composable
fun ProfileScreen(
    navController: NavController,
    userRepository: UserRepository // Make sure you're passing UserRepository here
) {
    //
    // Get the ViewModel instance
    val viewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory(userRepository) // Pass the UserRepository
    )

    // Trigger the fetch action to get the user
    LaunchedEffect(Unit) {
        viewModel.getUserById(1) // Fetch user by ID when the Composable is first launched
    }

    // Collect the selectedUser state to observe changes
    val user by viewModel.selectedUser.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        ProfileHeader(user)

        Spacer(modifier = Modifier.height(16.dp))

        ProfileOption(
            icon = PhosphorIcons.Regular.Pill,
            text = "Prescriptions"
        ) {
            navController.navigate("prescriptions/${user?.id}")
        }

        ProfileOption(
            icon = Icons.Default.Person,
            text = "Personal Information"
        ) {
            navController.navigate("personal_info/${user?.id}")
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
