package com.example.authentification.screen.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.Eye
import com.adamglin.phosphoricons.bold.EyeSlash
import com.example.core.theme.Gray50
import com.example.core.theme.Gray500
import com.example.core.theme.Gray600
import com.example.core.theme.Gray700
import com.example.core.theme.Gray800
import com.example.core.theme.Gray900
import com.example.core.theme.Primary100
import com.example.core.theme.Primary200
import com.example.core.theme.Primary300
import com.example.core.theme.Primary50
import com.example.core.theme.Primary500
import com.example.data.model.AuthResponse
import com.example.data.model.LoginRequest
import com.example.splashscreen.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.example.data.repository.AuthRepository
import com.example.data.retrofit.RetrofitInstance
import com.example.data.util.TokenManager
import com.example.data.viewModel.AuthViewModel

@Composable
fun LoginScreen(googleAuthHelper: googleAuthHelper, navController: NavController, onLoginSuccess: () -> Unit = {}) {
    val authRepository = remember { AuthRepository(RetrofitInstance.apiService) }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Companion.Factory(authRepository)
    )

    // States for form fields
    var username by remember { mutableStateOf("") } // Changed from email to username
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Collect states from ViewModel
    val loginState by authViewModel.loginState.collectAsState()
    val errorState by authViewModel.errorState.collectAsState()

    // Handle login response
    // Handle login response
    LaunchedEffect(loginState) {
        val currentLoginState = loginState
        if (currentLoginState != null && currentLoginState is AuthResponse) {
            isLoading = false
            Log.d("LoginScreen", "Full response: $currentLoginState")

            // Access token directly from root level
            Log.d("LoginScreen", "Access token raw: ${currentLoginState.access}")

            // Enhanced token and user data storage
            currentLoginState.access?.let {
                TokenManager.saveToken(it)
                Log.d("LoginScreen", "Access token saved: $it")
            }

            currentLoginState.refresh?.let {
                TokenManager.saveRefreshToken(it)
                Log.d("LoginScreen", "Refresh token saved: $it")
            }

            // Save user ID
            currentLoginState.user?.id?.let {
                TokenManager.saveUserId(it)
                Log.d("LoginScreen", "User ID saved: $it")
            }

            // Save user role
            currentLoginState.user?.role?.let {
                TokenManager.saveUserRole(it)
                Log.d("LoginScreen", "User role saved: $it")
            }

            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()

            onLoginSuccess()
            // Navigate to main screen or dashboard

                navController.navigate(Routes.DOCTOR_PARAMS){
                popUpTo(Routes.SIGN_IN) { inclusive = true }
            }

            // Clear login state to avoid navigation loop
            authViewModel.clearLoginState()
        }
    }
    // Handle error response
    LaunchedEffect(errorState) {
        if (errorState != null) {
            isLoading = false
            Toast.makeText(context, errorState, Toast.LENGTH_LONG).show()
            // Clear error state
            authViewModel.clearState()
        }
    }

    // Creating the launcher using rememberLauncherForActivityResult
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)

            Log.d("GoogleSignIn", "Success: ${account.email}")

            // Here you might want to extract username from email or make a separate API call
            username = account.email?.substringBefore("@") ?: ""
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Failed", e)
        }
    }

    // Background with glass effect
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Primary100,
                        Primary50
                    )
                )
            )
    ) {
        // Background circles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        drawCircle(
                            color = Color(0x3360A5FA), // Translucent Primary400
                            radius = size.minDimension * 0.8f,
                            center = center.copy(x = center.x * 0.2f, y = center.y * 0.3f)
                        )
                        drawCircle(
                            color = Color(0x33BFDBFE), // Translucent Primary200
                            radius = size.minDimension * 1.2f,
                            center = center.copy(x = center.x * 1.2f, y = center.y * 1.1f)
                        )
                        drawContent()
                    }
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "Don't have an account? Sign Up" text
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = Gray600
                )

                Text(
                    text = "Sign Up",
                    fontSize = 14.sp,
                    color = Primary500,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.SIGN_UP)
                    }
                )
            }

            // Main Content Card with improved Glassmorphism effect
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(
                        elevation = 24.dp,
                        spotColor = Color(0x1A000000),
                        ambientColor = Color(0x1A000000),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Username Field (changed from Email)
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = {
                            Text(
                                text = "Username",
                                color = Gray500,
                                textAlign = TextAlign.Start,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary500,
                            unfocusedBorderColor = Primary300,
                            cursorColor = Primary500,
                            unfocusedContainerColor = Gray50,
                            focusedContainerColor = Primary100
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                            color = Gray900
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text(
                                text = "Password",
                                color = Gray600,
                                textAlign = TextAlign.Start,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary500,
                            unfocusedBorderColor = Primary300,
                            cursorColor = Primary500,
                            unfocusedContainerColor = Gray50,
                            focusedContainerColor = Primary100
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Start,
                            fontSize = 14.sp,
                            color = Gray900
                        ),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) PhosphorIcons.Bold.Eye else PhosphorIcons.Bold.EyeSlash,
                                    contentDescription = "Toggle Password Visibility",
                                    tint = Primary500
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Remember Me and Forgot Password row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Primary500,
                                    uncheckedColor = Gray600
                                )
                            )
                            Text(
                                text = "Remember me",
                                color = Gray600,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "Forgot Password?",
                            color = Primary500,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                navController.navigate(Routes.FORGOT_PASS)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login Button
                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please enter username and password", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true
                            Log.d("LoginScreen", "Logging in user: $username")

                            val loginRequest = LoginRequest(
                                username = username,
                                password = password
                            )

                            authViewModel.login(loginRequest)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary500),
                        contentPadding = PaddingValues(0.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Login",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Or divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Primary300)
                        )
                        Text(
                            text = "Or",
                            color = Gray600,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            fontSize = 12.sp
                        )
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Primary300)
                        )
                    }

                    // Google Sign In Button
                    Button(
                        onClick = {
                            val signInIntent = googleAuthHelper.googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp
                        ),
                        border = BorderStroke(
                            1.dp,
                            Primary300
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Continue with Google",
                                color = Gray600,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}