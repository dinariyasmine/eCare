package com.example.authentification.screen.ui.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.CaretDown
import com.example.splashscreen.R
import com.example.authentification.screen.ui.screen.CustomTextField
import com.example.authentification.screen.ui.screen.DatePickerField
import com.example.core.theme.Gray900
import com.example.core.theme.Primary100
import com.example.core.theme.Primary200
import com.example.core.theme.Primary300
import com.example.core.theme.Primary400
import com.example.core.theme.Primary50
import com.example.core.theme.Primary500
import com.example.core.theme.Gray100
import com.example.core.theme.Gray500
import com.example.core.theme.Gray600
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun SignUp2Screen(googleAuthHelper: googleAuthHelper, navController: NavController) {
    // States for form fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var linkedin by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var socialMedia3 by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var clinicAddress by remember { mutableStateOf("") }
    var availabilitySchedule by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Doctor") } // Default to Doctor

    // Creating the launcher using rememberLauncherForActivityResult
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            // Handle the signed-in account (account.id, account.email, etc.)
            Log.d("GoogleSignIn", "Success: ${account.email}")


            email = account.email ?: ""
            firstName = account.givenName ?: ""
            lastName = account.familyName ?: ""
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
                text = "Sign Up",
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
                    // User Type Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(40.dp)
                            .background(Gray100, RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFFEFF0F6), RoundedCornerShape(20.dp)),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Doctor Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (userType == "Doctor") Color.White else Color.Transparent
                                )
                                .clickable { userType = "Doctor" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Doctor",
                                color = if (userType == "Doctor") Gray900 else Gray500,
                                fontWeight = if (userType == "Doctor") FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }

                        // Patient Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (userType == "Patient") Color.White else Color.Transparent
                                )
                                .clickable { userType = "Patient" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Patient",
                                color = if (userType == "Patient") Gray900 else Gray500,
                                fontWeight = if (userType == "Patient") FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Form Fields
                    // Specialty Dropdown
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

                    // Clinic Name Field
                    CustomTextField(
                        value = clinicName,
                        onValueChange = { clinicName = it },
                        placeholder = "Clinic Name",
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
                        placeholder = "Clinic Adress",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Using the new DatePickerField component
                    DatePickerField(
                        value = availabilitySchedule,
                        onValueChange = { availabilitySchedule = it },
                        placeholder = "Availability Schedule",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign Up Button
                    Button(
                        onClick = { /* Handle sign up logic */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary500
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Sign Up",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
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
                        border = androidx.compose.foundation.BorderStroke(
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