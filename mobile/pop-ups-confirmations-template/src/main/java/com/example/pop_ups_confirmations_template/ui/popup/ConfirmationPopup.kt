package com.example.pop_ups_confirmations_template.ui.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.ArrowLeft
import com.example.core.theme.Gray500
import com.example.core.theme.Gray900

@Composable
fun ConfirmationPopup(
    state: PopupState,
    onClose: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Back Button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = PhosphorIcons.Bold.ArrowLeft,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        // Popup Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(state.iconColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = state.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // Title
            Text(
                text = state.title,
                style = MaterialTheme.typography.displayMedium,
                color = Gray900,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                text = state.description,
                style = MaterialTheme.typography.headlineMedium,
                color = Gray500,
                modifier = Modifier.padding(horizontal = 8.dp),
                lineHeight = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // Action Button
            if (state is PopupState.Error) {
                Button(
                    onClick = state.onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, Color.Red, RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = state.buttonText,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            } else {
                Button(
                    onClick = state.onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)), // More vibrant blue matching the image
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = state.buttonText,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Progress Indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
            )
        }
    }
}