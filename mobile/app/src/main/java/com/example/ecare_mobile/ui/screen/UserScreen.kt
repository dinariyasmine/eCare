package com.example.ecare_mobile.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecare_mobile.ui.theme.ECareMobileTheme
import com.example.ecare_mobile.viewmodel.UserViewModel
import androidx.compose.material3.Text // Use Material3 for Text

@Composable
fun UserScreen(userViewModel: UserViewModel = viewModel()) {
    val users by userViewModel.users.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.fetchUsers()
    }

    ECareMobileTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Heading using Typography from Figma (Heading 1)
            Text(
                text = "User List",
                style = MaterialTheme.typography.displayLarge, // Heading 1 from Figma
                color = MaterialTheme.colorScheme.primary // Primary color from Figma
            )

            Spacer(modifier = Modifier.height(16.dp)) // Spacing between heading and list

            users.forEach { user ->
                // Body text using Typography from Figma (Body Large)
                Text(
                    text = "${user.name} - ${user.email}",
                    style = MaterialTheme.typography.bodyLarge, // Body Large from Figma
                    color = MaterialTheme.colorScheme.onBackground // Text color based on theme
                )

                Spacer(modifier = Modifier.height(8.dp)) // Spacing between items

                Divider(color = MaterialTheme.colorScheme.secondary) // Divider with secondary color
            }
        }
    }
}