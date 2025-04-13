package com.example.ecare_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.appointment.Greeting
import com.example.ecare_mobile.ui.screen.UserScreen
import com.example.ecare_mobile.ui.theme.ECareMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECareMobileTheme {
                Greeting(
                    name = "Android"
                )
            }
        }
    }
}
