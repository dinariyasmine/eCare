package com.example.appointment.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val title: String,
    val regularIcon: ImageVector,
    val boldIcon: ImageVector,
    val contentDescription: String? = null,
    val badgeCount: Int = 0
)
