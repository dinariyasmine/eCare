package com.example.doctorlisting


import com.example.doctorlisting.ui.screen.DoctorDetailScreen
import com.example.doctorlisting.ui.screen.DoctorListScreen

// androidApp/src/main/java/com/example/androidApp/Navigation.kt

import androidx.compose.runtime.Composable
import com.example.doctorlisting.BottomNavItem

sealed class BottomNavItem(val route: String, val label: String) {
    object Home : BottomNavItem("home", "Home")
    object Doctors : BottomNavItem("doctors", "Doctors")
}
