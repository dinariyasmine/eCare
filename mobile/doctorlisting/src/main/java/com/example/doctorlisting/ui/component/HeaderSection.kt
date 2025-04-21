package com.example.doctorlisting.ui.component

import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Notifications
import com.example.data.model.User

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun HeaderSection(user: User?, unreadNotifications: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Info
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            Image(
                painter = rememberAsyncImagePainter(   model = "url"),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Welcome Text
            Column {
                Text(
                    text = "Hi, Welcome Back,",
                 //  style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
                if (user != null) {
                    Text(
                        text = user.name,
                        //  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Notification Bell with Red Dot
        Box {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                modifier = Modifier
                    .size(28.dp)
            )
            if (unreadNotifications > 0) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red, CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}
