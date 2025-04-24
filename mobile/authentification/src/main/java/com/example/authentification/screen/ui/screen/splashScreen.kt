package com.example.authentification.screen.ui.screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splashscreen.R
import kotlinx.coroutines.delay
import com.example.core.theme.Primary50
import com.example.core.theme.Gray600
import com.example.core.theme.Gray700

@Composable
fun SplashScreen(onSplashComplete: () -> Unit = {}) {
    // Animation states
    var showLogo by remember { mutableStateOf(true) }
    var showAppName by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }
    var centerLogo by remember { mutableStateOf(true) }
    var moveContentUp by remember { mutableStateOf(false) }

    // Logo size animation
    val logoSize by animateDpAsState(
        targetValue = if (centerLogo) 120.dp else 80.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoSize"
    )

    // Content vertical offset animation
    val contentOffsetY by animateDpAsState(
        targetValue = if (moveContentUp) (-40).dp else 0.dp,
        animationSpec = tween(durationMillis = 500),
        label = "contentOffsetY"
    )

    // Opacity animations
    val appNameAlpha by animateFloatAsState(
        targetValue = if (showAppName) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "appNameAlpha"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (showTagline) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "taglineAlpha"
    )

    // Animation sequence
    LaunchedEffect(key1 = true) {
        delay(500) // Initial delay
        centerLogo = false // Start shrinking logo
        delay(800)
        showAppName = true // Fade in app name
        delay(800)
        moveContentUp = true // Move content up
        delay(500)
        showTagline = true // Fade in tagline
        delay(1500)
        onSplashComplete() // Notify completion
    }

    // Light blue background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary50),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = contentOffsetY)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "eCare Logo",
                    modifier = Modifier.size(logoSize)
                )

                // App name
                Text(
                    text = "eCare",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gray600,
                    modifier = Modifier
                        .alpha(appNameAlpha)
                        .padding(start = 8.dp)
                )
            }

            // Tagline
            Text(
                text = "Book, Track, Heal – Anytime, Anywhere!",
                fontSize = 14.sp,
                color = Gray700,
                modifier = Modifier
                    .alpha(taglineAlpha)
                    .padding(top = 16.dp)
            )
        }
    }
}