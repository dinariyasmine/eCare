package com.example.notifications.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.ArrowsClockwise
import com.adamglin.phosphoricons.bold.Check
import com.adamglin.phosphoricons.bold.FilePlus
import com.adamglin.phosphoricons.bold.FileText
import com.adamglin.phosphoricons.bold.Hourglass
import com.adamglin.phosphoricons.bold.Pill
import com.adamglin.phosphoricons.bold.X
import com.example.core.theme.Primary500
import com.example.core.theme.Error600
import com.example.core.theme.Info500
import com.example.core.theme.Warning400
import com.example.data.model.Notification
import com.example.data.model.NotificationType
import com.example.data.viewModel.NotificationTab
import com.example.data.viewModel.NotificationViewModel
import com.example.notifications.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(viewModel: NotificationViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Tab selector - styled as in your UI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TabButton(
                text = "Unread",
                isSelected = selectedTab == NotificationTab.UNREAD,
                onClick = { viewModel.selectTab(NotificationTab.UNREAD) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Read",
                isSelected = selectedTab == NotificationTab.READ,
                onClick = { viewModel.selectTab(NotificationTab.READ) },
                modifier = Modifier.weight(1f)
            )
        }

        // Add "Mark All as Read" button when viewing unread notifications
        if (selectedTab == NotificationTab.UNREAD && uiState.notifications.isNotEmpty()) {
            OutlinedButton(
                onClick = {
                    Log.d("NotificationsScreen", "Mark all as read clicked")
                    viewModel.markAllAsRead()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Primary500,
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Primary500)
                )
            ) {
                Icon(
                    imageVector = PhosphorIcons.Bold.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Primary500
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Mark All as Read",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Notifications content
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary500)
            }
        } else if (uiState.notifications.isEmpty()) {
            EmptyNotificationsState()
        } else {
            NotificationsList(
                notificationGroups = uiState.notifications,
                onNotificationClick = { notification ->
                    Log.d("NotificationsScreen", "Notification clicked: ${notification.id}")
                    viewModel.markAsRead(notification.id)
                }
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Primary500 else Color.White,
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmptyNotificationsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Use PNG image from drawable instead of icon
        Image(
            painter = painterResource(id = R.drawable.ic_empty_notifications),
            contentDescription = "No notifications",
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No New Notifications",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You're all caught up! No new notifications.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick  // Make the entire card clickable
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
                    .background(getNotificationColor(notification.notificationType)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.notificationType),
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
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Gray
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
@Composable
private fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.APPOINTMENT_CONFIRMED -> Primary500 // Green
        NotificationType.APPOINTMENT_RESCHEDULED -> Info500 // Blue
        NotificationType.APPOINTMENT_CANCELED -> Error600 // Red
        NotificationType.APPOINTMENT_REMINDER -> Color(0xFF9C27B0) // Purple
        NotificationType.PRESCRIPTION_CREATED,
        NotificationType.PRESCRIPTION_UPDATED,
        NotificationType.MEDICATION_REMINDER -> Warning400 // Orange
    }
}

@Composable
private fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.APPOINTMENT_CONFIRMED -> PhosphorIcons.Bold.Check
        NotificationType.APPOINTMENT_RESCHEDULED -> PhosphorIcons.Bold.ArrowsClockwise
        NotificationType.APPOINTMENT_CANCELED -> PhosphorIcons.Bold.X
        NotificationType.APPOINTMENT_REMINDER -> PhosphorIcons.Bold.Hourglass
        NotificationType.PRESCRIPTION_CREATED -> PhosphorIcons.Bold.FilePlus
        NotificationType.PRESCRIPTION_UPDATED -> PhosphorIcons.Bold.FileText
        NotificationType.MEDICATION_REMINDER -> PhosphorIcons.Bold.Pill
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
