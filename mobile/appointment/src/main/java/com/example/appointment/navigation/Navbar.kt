package com.example.appointment.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.bold.Bell
import com.adamglin.phosphoricons.bold.CalendarBlank
import com.adamglin.phosphoricons.bold.House
import com.adamglin.phosphoricons.bold.Stethoscope
import com.adamglin.phosphoricons.bold.User
import com.adamglin.phosphoricons.regular.Bell
import com.adamglin.phosphoricons.regular.CalendarBlank
import com.adamglin.phosphoricons.regular.House
import com.adamglin.phosphoricons.regular.Stethoscope
import com.adamglin.phosphoricons.regular.User
import com.example.core.theme.Primary500

@Composable
fun BottomNavBar(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(
            title = "Home",
            regularIcon = PhosphorIcons.Regular.House,
            boldIcon = PhosphorIcons.Bold.House,
            contentDescription = "Home"
        ),
        NavItem(
            title = "Medical",
            regularIcon = PhosphorIcons.Regular.Stethoscope,
            boldIcon = PhosphorIcons.Bold.Stethoscope,
            contentDescription = "Medical"
        ),
        NavItem(
            title = "Calendar",
            regularIcon = PhosphorIcons.Regular.CalendarBlank,
            boldIcon = PhosphorIcons.Bold.CalendarBlank,
            contentDescription = "Calendar"
        ),
        NavItem(
            title = "Notifications",
            regularIcon = PhosphorIcons.Regular.Bell,
            boldIcon = PhosphorIcons.Bold.Bell,
            contentDescription = "Notifications",
            badgeCount = 0
        ),
        NavItem(
            title = "Profile",
            regularIcon = PhosphorIcons.Regular.User,
            boldIcon = PhosphorIcons.Bold.User,
            contentDescription = "Profile"
        )
    )

    val activeColor = Color(0xFF2196F3)
    val inactiveColor = Color.Gray.copy(alpha = 0.6f)

    NavigationBar(
        modifier = modifier.height(64.dp),
        containerColor = Color(0xFFF8FAFC),
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = index == selectedItemIndex

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (item.badgeCount > 0 && !isSelected) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(text = item.badgeCount.toString())
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = item.regularIcon,
                                    contentDescription = item.contentDescription,
                                    tint = inactiveColor,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (isSelected) item.boldIcon else item.regularIcon,
                                contentDescription = item.contentDescription,
                                tint = if (isSelected) activeColor else inactiveColor,
                                modifier = Modifier.padding(bottom = 4.dp))
                        }

                        // Blue line indicator for selected item
                        AnimatedVisibility(visible = isSelected) {
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(2.dp)
                                    .background(activeColor))
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = activeColor,
                    unselectedIconColor = inactiveColor,
                    indicatorColor = Color.Transparent))
        }
    }
}