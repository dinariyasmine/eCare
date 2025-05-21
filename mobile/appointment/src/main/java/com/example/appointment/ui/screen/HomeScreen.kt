package com.example.appointment.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onAppointmentsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Appointments")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onProfileClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Profile")
            }
        }
    }
} 