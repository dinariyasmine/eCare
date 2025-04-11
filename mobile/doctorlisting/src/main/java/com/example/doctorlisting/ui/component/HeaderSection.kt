package com.example.doctorlisting.ui.component

import androidx.compose.material.icons.filled.Notifications
import coil.compose.rememberAsyncImagePainter
import com.example.ecare_mobile.data.model.User

import com.adamglin.PhosphorIcons
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.doctorlisting.data.repository.DoctorsRepositoryImpl

import com.adamglin.phosphoricons.regular.InstagramLogo
import com.adamglin.phosphoricons.regular.LinkedinLogo
import com.adamglin.phosphoricons.regular.Phone
import com.adamglin.phosphoricons.regular.Envelope

import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Star
import com.example.doctorlisting.R

@Composable
fun HeaderSection(user: User, unreadNotifications: Int) {
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
//            Image(
//                painter = rememberAsyncImagePainter(   model = user.avatarUrl),
//                contentDescription = "Profile Picture",
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(CircleShape)
//            )

            Spacer(modifier = Modifier.width(12.dp))

            // Welcome Text
            Column {
                Text(
                    text = "Hi, Welcome Back,",
                  //  style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
                Text(
                    text = user.name,
                  //  style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
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
