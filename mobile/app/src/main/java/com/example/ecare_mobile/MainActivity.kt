package com.example.ecare_mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.bold.AirTrafficControl
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
import com.example.core.theme.ECareMobileTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if onboarding is completed

        setContent {
            ECareMobileTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItemIndex = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        // Content for each tab
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                0 -> Text("Home Screen")
                1 -> Text("Medical Screen")
                2 -> Text("Calendar Screen")
                3 -> Text("Notifications Screen")
                4 -> Text("Profile Screen")
            }
        }
    }
}

data class NavItem(
    val title: String,
    val regularIcon: ImageVector,
    val boldIcon: ImageVector,
    val contentDescription: String? = null,
    val badgeCount: Int = 0
)

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

    val activeColor = Color(0xFF2196F3) // Blue color as shown in the image
    val inactiveColor = Color.Gray.copy(alpha = 0.6f)

    NavigationBar(
        modifier = modifier.height(64.dp),
        containerColor = Color(0xFFF8FAFC), // Light background color as in the image
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
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Blue line indicator for selected item
                        AnimatedVisibility(visible = isSelected) {
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(2.dp)
                                    .background(activeColor)
                            )
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = activeColor,
                    unselectedIconColor = inactiveColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
