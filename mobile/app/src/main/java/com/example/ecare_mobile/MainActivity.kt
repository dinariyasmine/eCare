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
import com.example.ecare_mobile.ui.screen.UserScreen
import com.example.ecare_mobile.ui.theme.ECareMobileTheme
import com.example.onboardingscreens.getWelcomeMessage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message = getWelcomeMessage()
        Log.d("MainActivity", message)
        setContent {
            ECareMobileTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    UserScreen()
                    Text(text = message)
                    Icon(
                        imageVector = PhosphorIcons.Bold.AirTrafficControl,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}
