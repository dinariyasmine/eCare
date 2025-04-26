package com.example.ecare_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.ECareMobileTheme
import com.example.pop_ups_confirmations_template.ui.popup.ConfirmationPopup
import com.example.pop_ups_confirmations_template.ui.popup.PopupState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECareMobileTheme {
                PopupDemoScreen()
            }
        }
    }
}

@Composable
fun PopupDemoScreen() {
    // State to track which popup is currently showing
    var currentPopup by remember { mutableStateOf<PopupType?>(null) }

    // Define popup states
    val errorState = PopupState.createErrorState(
        title = "XXXXXX Failed !",
        description = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxx",
        buttonText = "xxxxxxxxxx",
        onAction = { currentPopup = PopupType.SUCCESS }
    )

    val successState = PopupState.createSuccessState(
        title = "XXXXXX",
        description = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
        buttonText = "xxxxxxxxxx",
        onAction = { currentPopup = null }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Home screen with buttons to trigger popups
        AnimatedVisibility(
            visible = currentPopup == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Popup Demo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                Button(
                    onClick = { currentPopup = PopupType.ERROR },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Show Error Popup",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { currentPopup = PopupType.SUCCESS },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Show Success Popup",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Error popup
        AnimatedVisibility(
            visible = currentPopup == PopupType.ERROR,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ConfirmationPopup(
                state = errorState,
                onClose = { currentPopup = null }
            )
        }

        // Success popup
        AnimatedVisibility(
            visible = currentPopup == PopupType.SUCCESS,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ConfirmationPopup(
                state = successState,
                onClose = { currentPopup = null }
            )
        }
    }
}

// Enum to track which popup to show
enum class PopupType {
    ERROR,
    SUCCESS
}