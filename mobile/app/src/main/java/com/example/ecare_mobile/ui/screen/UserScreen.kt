package com.example.ecare_mobile.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.User
import com.example.data.repository.UserRepository
import com.example.data.viewModel.UserViewModel

@Composable
fun UserScreen(userRepository: UserRepository) {
    // Create ViewModel using the Factory to provide the repository
    val viewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory(userRepository)
    )

    // Collect state from ViewModel
    val users by viewModel.users.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // UI State
    var selectedUser by remember { mutableStateOf<User?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "User Management",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Error message
        error?.let { errorMsg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = errorMsg,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Button(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            }
        }

        // Loading indicator
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // User list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(users) { user ->
                    UserCard(
                        user = user,
                        onEditClick = { selectedUser = user },
                        onDeleteClick = { viewModel.deleteUser(user.id) }
                    )
                }
            }

            // Add button
            Button(
                onClick = { /* Open add user dialog */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Add New User")
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Role: ${user.role}",
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditClick) {
                    Text("Edit")
                }
                TextButton(onClick = onDeleteClick) {
                    Text("Delete")
                }
            }
        }
    }
}
