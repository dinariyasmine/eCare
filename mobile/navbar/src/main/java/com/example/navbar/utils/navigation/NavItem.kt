package com.example.navbar.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val contentDescription: String? = null,
    val badgeCount: Int = 0
)

