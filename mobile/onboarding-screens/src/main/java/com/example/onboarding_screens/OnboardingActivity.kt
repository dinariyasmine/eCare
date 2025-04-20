package com.example.onboarding_screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import com.example.core.theme.ECareMobileTheme
import com.example.core.theme.Gray400
import com.example.core.theme.Gray500
import com.example.core.theme.Gray900
import com.example.core.theme.Primary500
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECareMobileTheme {
                OnboardingScreen(
                    onFinish = {
                        // Save that onboarding is complete
                        OnboardingPrefs.setOnboardingComplete(this)

                        // Navigate to main activity
                        finish()
                        //removed navigation to main activity here
                    }
                )
            }
        }
    }
}

// Simple preferences helper for onboarding
object OnboardingPrefs {
    private const val PREF_NAME = "onboarding_prefs"
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    fun resetOnboarding(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_ONBOARDING_COMPLETE).apply()
    }
    fun setOnboardingComplete(context: android.content.Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
    }

    fun isOnboardingComplete(context: android.content.Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageResId: Int  // Using resource ID for Figma PNG images
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {

    // Create our onboarding pages with PNG images
    val pages = listOf(
        OnboardingPage(
            title = "Healthcare, Just a Tap Away!",
            description = "Get easy access to a wide range of healthcare services—all from the comforts of your home. No more long waits. Just seamless healthcare at your fingertips.",
            imageResId = R.drawable.obs_1
        ),
        OnboardingPage(
            title = "Book Appointments in Seconds",
            description = "No more waiting in long queues! Easily find the right doctor, schedule appointments in seconds, and get the care you need without the hassle.",
            imageResId = R.drawable.obs_2
        ),
        OnboardingPage(
            title = "Your Health, Always Within Reach",
            description = "Stay in control of your health with easy appointment tracking, timely reminders, and seamless access to your medical history—all in one place.",
            imageResId = R.drawable.obs_3
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    var showButton by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        showButton = pagerState.currentPage == pages.size - 1
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { position ->
            OnboardingPage(page = pages[position])
        }

        BottomSection(
            size = pages.size,
            index = pagerState.currentPage,
            showButton = showButton,
            onSkip = { onFinish() },
            onNext = {
                if (pagerState.currentPage < pages.size - 1) {
                    // If not the last page, navigate to next page
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }

                } else {
                    // If last page, finish onboarding
                    onFinish()
                }
            },
            onContinue = { onFinish() }
        )
    }
}

@Composable
fun OnboardingPage(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Using Image composable with the PNG resource from Figma
            Image(
                painter = painterResource(id = page.imageResId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = page.title,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
            color = Gray900
        )

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
            color  = Gray500
        )
    }
}

@Composable
fun BottomSection(
    size: Int,
    index: Int,
    showButton: Boolean,
    onSkip: () -> Unit,
    onNext: () -> Unit,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Skip button with default color
        TextButton(
            onClick = { onSkip() },
            modifier = Modifier.align(Alignment.CenterStart),
            enabled = !showButton,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Gray400
            )
        ) {
            Text(
                text = "Skip" ,
                style = MaterialTheme.typography.bodyMedium)
        }

        // Indicators
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(size) { i ->
                Box(
                    modifier = Modifier
                        .size(
                            width = if (i == index) 24.dp else 10.dp,
                            height = 10.dp
                        )
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (i == index) Primary500 else Color.LightGray)
                )
            }
        }

        // Next or Continue button with custom blue color
        TextButton(
            onClick = { if (showButton) onContinue() else onNext() },

            modifier = Modifier.align(Alignment.CenterEnd),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Primary500
            )
        ) {
            Text(
                text = if (showButton) "Continue" else "Next",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


