package com.example.doctorlisting.ui.component

import androidx.compose.material.icons.filled.Notifications
import coil.compose.rememberAsyncImagePainter
import com.example.ecare_mobile.data.model.User

import com.adamglin.PhosphorIcons
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavController
import com.example.doctorlisting.data.model.Doctor
import com.example.doctorlisting.ui.screen.DoctorCard

@Composable
fun DoctorList(
    doctors: List<Doctor>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        doctors.forEach { doctor ->
            DoctorCard(doctor, navController)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
