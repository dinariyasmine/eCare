// mobile/notifications/src/main/java/com/example/notifications/ui/NotificationsScreen.kt
package com.example.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Notification
import com.example.data.model.NotificationType
import com.example.data.viewModel.NotificationTab
import com.example.data.viewModel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(viewModel: NotificationViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Tab selector
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == NotificationTab.UNREAD,
                onClick = { viewModel.selectTab(NotificationTab.UNREAD) },
                text = { Text("Unread") }
            )
            Tab(
                selected = selectedTab == NotificationTab.READ,
                onClick = { viewModel.selectTab(NotificationTab.READ) },
                text = { Text("Read") }
            )
        }

        // Notifications content
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.notifications.isEmpty()) {
            EmptyNotificationsState()
        } else {
            NotificationsList(
                notificationGroups = uiState.notifications,
                onNotificationClick = { viewModel.markAsRead(it.id) }
            )
        }
    }
}

@Composable
fun EmptyNotificationsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Replace with your actual icon resource
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_info),
                contentDescription = "No notifications",
                modifier = Modifier.size(100.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No New Notifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You're all caught up! No new notifications.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun NotificationsList(
    notificationGroups: Map<String, List<Notification>>,
    onNotificationClick: (Notification) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        notificationGroups.forEach { (dateGroup, notifications) ->
            item {
                Text(
                    text = dateGroup,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { onNotificationClick(notification) }
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on notification type
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getNotificationColor(notification.type)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getNotificationIcon(notification.type)),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = notification.description,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Time ago
            Text(
                text = formatTimeAgo(notification.timestamp),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

// Helper functions
private fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.APPOINTMENT_CONFIRMED -> Color(0xFF4CAF50) // Green
        NotificationType.APPOINTMENT_RESCHEDULED -> Color(0xFF2196F3) // Blue
        NotificationType.APPOINTMENT_CANCELED -> Color(0xFFF44336) // Red
        NotificationType.APPOINTMENT_REMINDER -> Color(0xFF9C27B0) // Purple
        NotificationType.PRESCRIPTION_CREATED,
        NotificationType.PRESCRIPTION_UPDATED,
        NotificationType.MEDICATION_REMINDER -> Color(0xFFFF9800) // Orange
    }
}

private fun getNotificationIcon(type: NotificationType): Int {
    // Replace with your actual icon resources
    return when (type) {
        NotificationType.APPOINTMENT_CONFIRMED -> android.R.drawable.ic_dialog_info
        NotificationType.APPOINTMENT_RESCHEDULED -> android.R.drawable.ic_dialog_info
        NotificationType.APPOINTMENT_CANCELED -> android.R.drawable.ic_dialog_alert
        NotificationType.APPOINTMENT_REMINDER -> android.R.drawable.ic_dialog_info
        NotificationType.PRESCRIPTION_CREATED,
        NotificationType.PRESCRIPTION_UPDATED,
        NotificationType.MEDICATION_REMINDER -> android.R.drawable.ic_dialog_info
    }
}

private fun formatTimeAgo(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    val diffInMinutes = diffInMillis / (60 * 1000)
    val diffInHours = diffInMillis / (60 * 60 * 1000)
    val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)

    return when {
        diffInMinutes < 60 -> "$diffInMinutes min ago"
        diffInHours < 24 -> "$diffInHours hours ago"
        diffInDays < 7 -> "$diffInDays days ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
}
