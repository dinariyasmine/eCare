package com.example.authentification.screen.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.Backspace
import com.adamglin.phosphoricons.bold.CaretLeft
import androidx.compose.foundation.border
import androidx.navigation.NavController
import com.example.splashscreen.R
import com.example.core.theme.*
import com.example.data.viewModel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun OTPScreen(
    navController: NavController,
    email: String,
    authViewModel: AuthViewModel
) {
    // State for OTP digits (5 digits)
    val otpDigits = remember { mutableStateListOf("", "", "", "", "") }
    var currentDigitIndex by remember { mutableIntStateOf(0) }
    var isVerifying by remember { mutableStateOf(false) }
    var showResendMessage by remember { mutableStateOf(false) }
    var showNetworkError by remember { mutableStateOf(false) }
    var networkErrorMessage by remember { mutableStateOf("") }

    // Collect state from ViewModel
    val otpVerificationState by authViewModel.otpVerificationState.collectAsState()
    val errorState by authViewModel.errorState.collectAsState()

    // Handle OTP verification result
    LaunchedEffect(otpVerificationState) {
        otpVerificationState?.let {
            // OTP verification was successful
            // Navigate to the reset password screen
            // How OTPScreen should navigate to ResetPasswordScreen
            val completeOtp = otpDigits.joinToString("")
            navController.navigate("${Routes.RESET_PASS}/${email}/${completeOtp}")
            authViewModel.clearPasswordResetStates()
        }
    }

    // Handle error state
    LaunchedEffect(errorState) {
        errorState?.let {
            showNetworkError = true
            networkErrorMessage = it
            // Clear error after showing
            delay(5000)
            showNetworkError = false
            authViewModel.clearState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button and header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = PhosphorIcons.Bold.CaretLeft,
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        navController.navigate(Routes.SIGN_IN)
                    },
                tint = Primary500
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "eCare",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Gray900
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Title
        Text(
            text = "Enter code",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray900
        )

        // Email information
        Text(
            text = "We've sent a code to ${maskEmail(email)}",
            fontSize = 14.sp,
            color = Gray600,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        // OTP Input Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0 until 5) {
                OTPDigitBox(
                    value = otpDigits.getOrNull(i) ?: "",
                    isFocused = currentDigitIndex == i,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Network error message
        AnimatedVisibility(
            visible = showNetworkError,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = networkErrorMessage,
                fontSize = 14.sp,
                color = Error600,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Wrong code message with resend option
        AnimatedVisibility(
            visible = showResendMessage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val wrongCodeText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Error600)) {
                        append("Wrong code. ")
                    }
                    withStyle(style = SpanStyle(color = Primary500)) {
                        append("Resend")
                    }
                }

                Text(
                    text = wrongCodeText,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // Reset OTP and request new code
                        for (i in otpDigits.indices) {
                            otpDigits[i] = ""
                        }
                        currentDigitIndex = 0
                        showResendMessage = false
                        authViewModel.requestPasswordReset(email)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Keypad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (number in 1..3) {
                    KeypadButton(
                        text = number.toString(),
                        onClick = {
                            if (currentDigitIndex < 5) {
                                otpDigits[currentDigitIndex] = number.toString()
                                if (currentDigitIndex < 4) currentDigitIndex++
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (number in 4..6) {
                    KeypadButton(
                        text = number.toString(),
                        onClick = {
                            if (currentDigitIndex < 5) {
                                otpDigits[currentDigitIndex] = number.toString()
                                if (currentDigitIndex < 4) currentDigitIndex++
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (number in 7..9) {
                    KeypadButton(
                        text = number.toString(),
                        onClick = {
                            if (currentDigitIndex < 5) {
                                otpDigits[currentDigitIndex] = number.toString()
                                if (currentDigitIndex < 4) currentDigitIndex++
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Empty space for the left side
                Box(modifier = Modifier.weight(1f))

                // 0 button
                KeypadButton(
                    text = "0",
                    onClick = {
                        if (currentDigitIndex < 5) {
                            otpDigits[currentDigitIndex] = "0"
                            if (currentDigitIndex < 4) currentDigitIndex++
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                // Backspace button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Bold.Backspace,
                        contentDescription = "Backspace",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                if (currentDigitIndex > 0) {
                                    currentDigitIndex--
                                    otpDigits[currentDigitIndex] = ""
                                } else if (otpDigits[0].isNotEmpty()) {
                                    otpDigits[0] = ""
                                }
                            },
                        tint = Gray900
                    )
                }
            }
        }

        // "I didn't receive a code" message
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "I didn't receive a code. ",
                fontSize = 14.sp,
                color = Gray600
            )

            TextButton(onClick = {
                // Resend code logic
                authViewModel.requestPasswordReset(email)
                for (i in otpDigits.indices) {
                    otpDigits[i] = ""
                }
                currentDigitIndex = 0
            }) {
                Text(
                    text = "Resend",
                    fontSize = 14.sp,
                    color = Primary500,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom indicator
        Box(
            modifier = Modifier
                .padding(8.dp)
                .width(134.dp)
                .height(5.dp)
                .background(Gray800, RoundedCornerShape(2.5.dp))
        )
    }

    // Verify OTP when all digits are filled
    LaunchedEffect(otpDigits.joinToString("")) {
        val completeOtp = otpDigits.joinToString("")
        if (completeOtp.length == 5 && !isVerifying) {
            isVerifying = true

            // Call the API to verify the OTP
            authViewModel.verifyOtp(email, completeOtp)

            // Add a small delay to avoid rapid firing
            delay(1000)
            isVerifying = false
        }
    }
}

@Composable
fun OTPDigitBox(
    value: String,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .height(50.dp)
            .width(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(White)
            .border(
                width = 1.5.dp,
                color = if (isFocused) Primary400 else Gray300,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Gray900
        )
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Gray900
        )
    }
}

// Helper function to mask email
private fun maskEmail(email: String): String {
    val parts = email.split("@")
    if (parts.size != 2) return email

    val username = parts[0]
    val domain = parts[1]

    val maskedUsername = if (username.length <= 2) {
        username
    } else {
        "${username.first()}${
            "*".repeat(
                minOf(username.length - 2, 5)
            )
        }${username.last()}"
    }

    return "$maskedUsername@$domain"
}