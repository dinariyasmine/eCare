package com.example.patientprofile.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.User

@Composable
fun ProfileHeader(user: User?) {
    user?.let {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {

                Text(it.name, style = MaterialTheme.typography.titleMedium)
                Text(it.email, style = MaterialTheme.typography.bodyMedium)
                Text(it.phone, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
