package com.example.authentification.screen.ui.screen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.Eye
import com.adamglin.phosphoricons.bold.EyeSlash
import com.example.splashscreen.R
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavController
import com.adamglin.phosphoricons.bold.CaretLeft
import com.example.authentification.screen.ui.screen.Routes
import com.example.core.theme.Gray900
import com.example.core.theme.Gray100
import com.example.core.theme.Primary500
import com.example.core.theme.Gray500
import com.example.core.theme.Gray300
import com.example.core.theme.White
import com.example.core.theme.Error600
import com.example.data.viewModel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    email: String,
    otpCode: String
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    // Collect states from ViewModel
    val passwordResetState by authViewModel.passwordResetState.collectAsState()
    val errorState by authViewModel.errorState.collectAsState()

    // Handle response from password reset
    LaunchedEffect(passwordResetState) {
        passwordResetState?.let {
            // Password reset was successful
            isLoading = false
            navController.navigate(Routes.SIGN_IN) {
                // Clear the back stack
                popUpTo(Routes.SIGN_IN) { inclusive = true }
            }
            authViewModel.clearPasswordResetStates()
        }
    }

    // Handle error state
    LaunchedEffect(errorState) {
        if (errorState != null) {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray100)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) { // Back button and header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                Icon(
                    imageVector = PhosphorIcons.Bold.CaretLeft,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    tint = Primary500
                )

                Spacer(modifier = Modifier.width(16.dp))
            }

            Spacer(modifier = Modifier.height(64.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = "Reset your password",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Please type something you'll remember",
                    fontSize = 14.sp,
                    color = Gray500,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // New Password
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        passwordError = null
                    },
                    placeholder = { Text("New password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) PhosphorIcons.Bold.Eye else PhosphorIcons.Bold.EyeSlash,
                                contentDescription = "Toggle Password",
                                tint = Primary500
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Primary500,
                        unfocusedIndicatorColor = Gray300,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        passwordError = null
                    },
                    placeholder = { Text("Confirm password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) PhosphorIcons.Bold.Eye else PhosphorIcons.Bold.EyeSlash,
                                contentDescription = "Toggle Confirm Password",
                                tint = Primary500
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Primary500,
                        unfocusedIndicatorColor = Gray300,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    )
                )

                // Display password error if any
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = Error600,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                // Display API error if any
                if (errorState != null) {
                    Text(
                        text = errorState!!,
                        color = Error600,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Reset Password Button
                Button(
                    onClick = {
                        // Validate passwords
                        when {
                            newPassword.length < 6 -> {
                                passwordError = "Password must be at least 6 characters"
                            }
                            newPassword != confirmPassword -> {
                                passwordError = "Passwords don't match"
                            }
                            else -> {
                                // Passwords valid, submit reset request
                                isLoading = true
                                keyboardController?.hide()
                                coroutineScope.launch {
                                    authViewModel.resetPassword(
                                        email = email,
                                        otpCode = otpCode,
                                        password = newPassword,
                                        password2 = confirmPassword
                                    )
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary500),
                    enabled = newPassword.isNotBlank() && confirmPassword.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = White
                        )
                    } else {
                        Text(
                            text = "Reset Password",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}