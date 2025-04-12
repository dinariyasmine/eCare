package com.example.ecare_mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.AirTrafficControl
import com.example.core.theme.ECareMobileTheme
import com.example.data.repository.UserRepository
import com.example.ecare_mobile.ui.screen.UserScreen
import com.example.onboardingscreens.getWelcomeMessage

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create UserRepository instance to pass to UserScreen
        val userRepository = UserRepository()

        // Get welcome message with null safety
        val message = getWelcomeMessage() ?: "Welcome to eCare Mobile"
        Log.d("MainActivity", message)

        setContent {
            ECareMobileTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Pass the repository to UserScreen
                    UserScreen(userRepository = userRepository)

                    Text(
                        text = message,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    Icon(
                        imageVector = PhosphorIcons.Bold.AirTrafficControl,
                        contentDescription = "App Icon",
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
