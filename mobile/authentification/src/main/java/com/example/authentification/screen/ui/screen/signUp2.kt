package com.example.authentification.screen.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.CaretDown
import com.example.core.theme.Gray100
import com.example.core.theme.Gray500
import com.example.core.theme.Gray600
import com.example.core.theme.Gray900
import com.example.core.theme.Primary100
import com.example.core.theme.Primary200
import com.example.core.theme.Primary300
import com.example.core.theme.Primary400
import com.example.core.theme.Primary50
import com.example.core.theme.Primary500
import com.example.data.model.RegistrationRequest
import com.example.data.repository.AuthRepository
import com.example.data.retrofit.RetrofitInstance
import com.example.data.viewModel.AuthViewModel
import com.example.splashscreen.R
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun SignUp2Screen(googleAuthHelper: googleAuthHelper, navController: NavController, authViewModel: AuthViewModel) {
    // Create repository and ViewModel

    val registrationState by authViewModel.registrationState.collectAsState()
    val errorState by authViewModel.errorState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentPassword by authViewModel.currentPassword.collectAsState()

    // Collect state from ViewModel



    // Local UI state
    var isLoading by remember { mutableStateOf(false) }
    var dataLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // States for form fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var linkedin by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var socialMedia3 by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var clinicId by remember { mutableStateOf(15) } // Default to first clinic
    var clinicAddress by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Doctor") } // Default to Doctor

    // Map clinic names to IDs - this would typically come from an API
    val clinics = remember {
        mapOf(
            "Clinic A" to 15,
            "Clinic B" to 16,
            "Clinic C" to 17,
            "Clinic D" to 18
        )
    }

    // Retrieve doctor information when the screen is first loaded
    LaunchedEffect(Unit) {
        Log.d("SignUp2Screen", "Screen composed, checking for user data...")

        // Get the stored user data
        val user = currentUser

        if (user != null) {
            Log.d("SignUp2Screen", "Found user data: ${user.name}, ${user.email}")
            val nameParts = user.name.split(" ")
            firstName = nameParts.getOrNull(0) ?: ""
            lastName = nameParts.getOrElse(1) { "" }
            email = user.email
            dataLoaded = true
        } else {
            Log.d("SignUp2Screen", "User data is null, will try to retrieve it")

            // Wait a moment and check again (sometimes state initialization is delayed)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                val delayedUser = authViewModel.currentUser.value
                if (delayedUser != null) {
                    Log.d("SignUp2Screen", "Retrieved user data after delay: ${delayedUser.name}")
                    val nameParts = delayedUser.name.split(" ")
                    firstName = nameParts.getOrNull(0) ?: ""
                    lastName = nameParts.getOrElse(1) { "" }
                    email = delayedUser.email
                    dataLoaded = true
                } else {
                    Log.e("SignUp2Screen", "User data still null after delay")
                    Toast.makeText(context, "Failed to retrieve user data. Please go back and try again.", Toast.LENGTH_LONG).show()
                }
            }, 500) // Longer delay to ensure data is available
        }
    }

    // Handle registration response
    LaunchedEffect(registrationState, errorState) {
        when {
            registrationState != null && isLoading -> {
                isLoading = false
                Toast.makeText(context, "Doctor registration successful!", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.SIGN_IN) {
                    popUpTo(Routes.SIGN_UP) { inclusive = true }
                }
                authViewModel.clearState()
                authViewModel.clearUserData()
            }
            errorState != null && isLoading -> {
                isLoading = false
                Toast.makeText(context, "Error: $errorState", Toast.LENGTH_LONG).show()
                authViewModel.clearState()
            }
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
        // Background circles like in the SVG
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
                text = "Doctor Information",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "Already have an account? Login" text
            val annotatedString = buildAnnotatedString {
                append("Already have an account? ")
                withStyle(style = SpanStyle(
                    color = Primary500,
                    fontWeight = FontWeight.Medium
                )) {
                    append("Login")
                }
            }

            Text(
                text = annotatedString,
                fontSize = 14.sp,
                color = Gray600,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clickable { navController.navigate(Routes.SIGN_IN) }
            )

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
                    // User Type Selector (keeping it but disabling interaction since this is the doctor registration screen)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(40.dp)
                            .background(Gray100, RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFFEFF0F6), RoundedCornerShape(20.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Doctor Button (always selected in this screen)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Doctor",
                                color = Gray900,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }

                        // Patient Button (disabled in this screen)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Transparent)
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Patient",
                                color = Gray500,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Form Fields
                    // Specialty Dropdown - Important for doctor registration
                    CustomTextField(
                        value = specialty,
                        onValueChange = { specialty = it },
                        placeholder = "Specialty",
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = PhosphorIcons.Bold.CaretDown,
                                contentDescription = "CaretDown",
                                tint = Primary500
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Brief description about your practice
                    CustomTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "Brief description about your practice",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // LinkedIn Field
                    CustomTextField(
                        value = linkedin,
                        onValueChange = { linkedin = it },
                        placeholder = "LinkedIn",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Instagram Field
                    CustomTextField(
                        value = instagram,
                        onValueChange = { instagram = it },
                        placeholder = "Instagram",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Social Media 3 Field
                    CustomTextField(
                        value = socialMedia3,
                        onValueChange = { socialMedia3 = it },
                        placeholder = "Social Media 3",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // For testing, just use clinic ID 15
                    Text(
                        text = "Using Clinic ID: 15 (for testing)",
                        color = Gray600,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Clinic Name Field - this is important for the API (clinicId)
                    CustomTextField(
                        value = clinicName,
                        onValueChange = {
                            clinicName = it
                            // For testing, always use ID 15
                            clinicId = 15
                        },
                        placeholder = "Clinic Name (Optional for testing)",
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = PhosphorIcons.Bold.CaretDown,
                                contentDescription = "CaretDown",
                                tint = Primary500
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Clinic Address Field
                    CustomTextField(
                        value = clinicAddress,
                        onValueChange = { clinicAddress = it },
                        placeholder = "Clinic Address (Optional for testing)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Complete Registration Button
                    Button(
                        onClick = {
                            if (specialty.isNotBlank()) {
                                isLoading = true
                                Log.d("SignUp2Screen", "Starting doctor registration process")

                                // Retrieve stored user data
                                val userData = currentUser
                                val storedPassword = currentPassword

                                Log.d("SignUp2Screen", "User data present: ${userData != null}")
                                Log.d("SignUp2Screen", "Password present: ${storedPassword != null}")

                                if (userData != null && storedPassword != null) {
                                    Log.d("SignUp2Screen", "Creating doctor registration request")
                                    Log.d("SignUp2Screen", "Username: ${userData.username}")
                                    Log.d("SignUp2Screen", "Email: ${userData.email}")
                                    Log.d("SignUp2Screen", "Specialty: $specialty")
                                    Log.d("SignUp2Screen", "Clinic ID: $clinicId")

                                    // Create doctor registration request with specialty and clinic_id
                                    val request = RegistrationRequest(
                                        username = userData.username,
                                        email = userData.email,
                                        password = storedPassword,
                                        password2 = storedPassword,
                                        name = userData.name,
                                        phone = userData.phone,
                                        address = userData.address,
                                        birth_date = userData.birth_date,
                                        role = "doctor",
                                        specialty = specialty,
                                        clinic_id = clinicId
                                    )

                                    // Register doctor with specialty and clinic info
                                    Log.d("SignUp2Screen", "Calling registerDoctor")
                                    authViewModel.registerDoctor(request)
                                } else {
                                    isLoading = false
                                    Log.e("SignUp2Screen", "User data or password is null")
                                    Toast.makeText(context, "User data not found, please start over", Toast.LENGTH_SHORT).show()
                                    // Ensure clean navigation back to sign up page
                                    navController.navigate(Routes.SIGN_UP) {
                                        popUpTo(Routes.SIGN_UP) { inclusive = true }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please fill in your specialty", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary500
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Complete Registration",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // Bottom indicator
            Box(
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 16.dp)
                    .width(134.dp)
                    .height(5.dp)
                    .background(Gray900, RoundedCornerShape(2.5.dp))
            )
        }
    }
}