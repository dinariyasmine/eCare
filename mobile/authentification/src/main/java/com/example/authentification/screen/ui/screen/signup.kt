package com.example.authentification.screen.ui.screen

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.CalendarDots
import com.adamglin.phosphoricons.bold.CaretDown
import com.adamglin.phosphoricons.bold.Eye
import com.adamglin.phosphoricons.bold.EyeSlash
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
import com.example.core.theme.White
import com.example.data.model.RegistrationRequest
import com.example.data.repository.AuthRepository
import com.example.data.retrofit.RetrofitInstance
import com.example.data.viewModel.AuthViewModel
import com.example.data.viewModel.ClinicViewModel
import com.example.splashscreen.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.data.repository.ClinicRepository
import com.example.data.model.SocialMedia
import com.example.data.repository.SocialMediaRepository
import com.example.data.util.TokenManager

@Composable
fun SignUpScreen(googleAuthHelper: googleAuthHelper, navController: NavController) {
    /// Get ClinicViewModel instance
    val clinicViewModel: ClinicViewModel = viewModel(
        factory = ClinicViewModel.Factory(ClinicRepository())
    )
    var clinicQuery by remember { mutableStateOf("") }


// Collect states from ViewModel
    val clinics = clinicViewModel.clinics.collectAsState().value
    val loading = clinicViewModel.loading.collectAsState().value
    val error = clinicViewModel.error.collectAsState().value

// Use LaunchedEffect to fetch clinics when the screen appears
    LaunchedEffect(key1 = Unit) {
        clinicViewModel.fetchAllClinics()
    }

// Compute filtered clinics based on user query
    val filteredClinics = remember(clinics, clinicQuery) {
        if (clinicQuery.isBlank()) {
            clinics
        } else {
            clinics.filter { it.name.contains(clinicQuery, ignoreCase = true) }
        }
    }



    // Create repository and ViewModel
    val authRepository = remember { AuthRepository(RetrofitInstance.apiService) }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Companion.Factory(authRepository)
    )

    // Collect state from ViewModel
    val registrationState by authViewModel.registrationState.collectAsState()
    val errorState by authViewModel.errorState.collectAsState()

    // Local UI state
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Common fields for both doctor and patient
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var userType by remember { mutableStateOf("Patient") }
    var dateOfBirth by remember { mutableStateOf("") }

    // Doctor-specific fields
    var specialty by remember { mutableStateOf("") }
    var specialtyQuery by remember { mutableStateOf("") }
    var isSpecialtyDropdownExpanded by remember { mutableStateOf(false) }
    var linkedin by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }
    var socialMedia3 by remember { mutableStateOf("") }
    var clinicName by remember { mutableStateOf("") }
    var clinicId by remember { mutableStateOf(15) } // Default clinic ID
    var isClinicDropdownExpanded by remember { mutableStateOf(false) }

    // For date picker
    var showDatePicker by remember { mutableStateOf(false) }

    // List of medical specialties
    val medicalSpecialties = listOf(
        "Cardiology",
        "Dermatology",
        "Endocrinology",
        "Family Medicine",
        "Gastroenterology",
        "Hematology",
        "Infectious Disease",
        "Internal Medicine",
        "Nephrology",
        "Neurology",
        "Obstetrics and Gynecology",
        "Oncology",
        "Ophthalmology",
        "Orthopedics",
        "Otolaryngology (ENT)",
        "Pediatrics",
        "Psychiatry",
        "Pulmonology",
        "Radiology",
        "Urology"
    )

    // Filtered specialties based on user input
    val filteredSpecialties = remember(specialtyQuery) {
        if (specialtyQuery.isEmpty()) {
            medicalSpecialties
        } else {
            medicalSpecialties.filter { it.contains(specialtyQuery, ignoreCase = true) }
        }
    }



    // Handle registration response
    LaunchedEffect(registrationState, errorState) {
        val reg = registrationState  // local variable to enable smart cast

        when {
            reg != null && isLoading -> {
                isLoading = false

                // In your logs, we can see that AuthViewModel has received the full response
                // but it's parsing it into the AuthResponse incorrectly
                // The response actually has the structure with nested tokens
                // We need to fix this in the AuthViewModel instead

                // Check logs for debugging
                Log.d("SignupScreen", "Full registration response: $reg")
                Log.d("SignupScreen", "Access token raw: ${reg.access}")

                // Use the AuthResponse structure directly like in login
                reg.access?.let {
                    TokenManager.saveToken(it)
                    Log.d("SignupScreen", "Access token saved: $it")
                }

                reg.refresh?.let {
                    TokenManager.saveRefreshToken(it)
                    Log.d("SignupScreen", "Refresh token saved: $it")
                }

                // Save user ID
                reg.user?.id?.let {
                    TokenManager.saveUserId(it)
                    Log.d("SignupScreen", "User ID saved: $it")
                }

                // Save user role
                reg.user?.role?.let {
                    TokenManager.saveUserRole(it)
                    Log.d("SignupScreen", "User role saved: $it")
                }

                // If this was a doctor registration and we have social media links
                if (userType == "Doctor" && (linkedin.isNotBlank() || instagram.isNotBlank() || socialMedia3.isNotBlank())) {
                    val doctorId = reg.user?.id

                    if (doctorId != null) {
                        val socialMediaRepository = SocialMediaRepository()

                        if (linkedin.isNotBlank()) {
                            socialMediaRepository.createSocialMedia(
                                SocialMedia(id = 0, doctor_id = doctorId, name = "LinkedIn", link = linkedin)
                            )
                        }

                        if (instagram.isNotBlank()) {
                            socialMediaRepository.createSocialMedia(
                                SocialMedia(id = 0, doctor_id = doctorId, name = "Instagram", link = instagram)
                            )
                        }

                        if (socialMedia3.isNotBlank()) {
                            socialMediaRepository.createSocialMedia(
                                SocialMedia(id = 0, doctor_id = doctorId, name = "Other", link = socialMedia3)
                            )
                        }
                    }
                }

                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()

                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SIGN_UP) { inclusive = true }
                }

                authViewModel.clearState()
            }

            errorState != null && isLoading -> {
                isLoading = false
                Toast.makeText(context, errorState ?: "An error occurred", Toast.LENGTH_LONG).show()
                authViewModel.clearState()
            }
        }
    }


    // Google sign-in launcher
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
            username = account.email?.substringBefore("@") ?: ""
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Failed", e)
        }
    }

    // Define validation function for common fields
    val validateCommonFields = {
        username.isNotBlank() &&
                email.isNotBlank() && email.contains("@") &&
                firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                password.isNotBlank() && password.length >= 8 &&
                password == confirmPassword &&
                dateOfBirth.isNotBlank() &&
                phoneNumber.isNotBlank() &&
                address.isNotBlank()
    }

    // Additional validation for doctor fields
    val validateDoctorFields = {
        validateCommonFields() && specialty.isNotBlank() && clinicName.isNotBlank()
    }

    // Function to format date for API
    val formatDateForApi = { dateString: String ->
        try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    // Background with glass effect
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Primary100, // Color(0xFFE3F2FD)
                        Primary50   // Color(0xFFE8F5FF)
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
                    .clickable {
                        navController.navigate(Routes.SIGN_IN)
                    }
            )

            // Main Content Card with Glassmorphism effect
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

                    // Common Form Fields
                    // Username
                    CustomTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "Username",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // First Name
                    CustomTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "First Name",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Last Name
                    CustomTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Last Name",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Email",
                        keyboardType = KeyboardType.Email,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address
                    CustomTextField(
                        value = address,
                        onValueChange = { address = it },
                        placeholder = "Address",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date of Birth Field
                    DatePickerField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        placeholder = "Date of Birth (DD/MM/YYYY)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone Number with Country Code
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Country Code Selector
                        Box(
                            modifier = Modifier
                                .weight(0.3f)
                                .height(50.dp)
                                .border(
                                    width = 1.dp,
                                    color = Primary300,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .background(Gray100)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.flags),
                                    contentDescription = "Algeria Flag",
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                )
                                Text(
                                    text = "+213",
                                    modifier = Modifier.padding(start = 4.dp),
                                    color = Gray600,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Phone Number Field
                        CustomTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            placeholder = "Phone Number",
                            keyboardType = KeyboardType.Phone,
                            modifier = Modifier.weight(0.7f)
                        )
                    }

                    // Doctor-specific fields - only show when Doctor is selected
                    if (userType == "Doctor") {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Specialty Field with searchable dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CustomTextField(
                                value = specialtyQuery,
                                onValueChange = {
                                    specialtyQuery = it
                                    if (!isSpecialtyDropdownExpanded) {
                                        isSpecialtyDropdownExpanded = true
                                    }
                                },
                                placeholder = "Specialty",
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isSpecialtyDropdownExpanded = !isSpecialtyDropdownExpanded
                                    }) {
                                        Icon(
                                            imageVector = PhosphorIcons.Bold.CaretDown,
                                            contentDescription = "Select Specialty",
                                            tint = Primary500
                                        )
                                    }
                                }
                            )

                            androidx.compose.material3.DropdownMenu(
                                expanded = isSpecialtyDropdownExpanded,
                                onDismissRequest = { isSpecialtyDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(White)
                            ) {
                                if (filteredSpecialties.isEmpty()) {
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("No matching specialties") },
                                        onClick = { /* Do nothing */ }
                                    )
                                } else {
                                    filteredSpecialties.forEach { specialtyOption ->
                                        androidx.compose.material3.DropdownMenuItem(
                                            text = { Text(specialtyOption) },
                                            onClick = {
                                                specialty = specialtyOption
                                                specialtyQuery = specialtyOption
                                                isSpecialtyDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Clinic Name Field with searchable dropdown
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CustomTextField(
                                value = clinicQuery,
                                onValueChange = {
                                    clinicQuery = it
                                    if (!isClinicDropdownExpanded) {
                                        isClinicDropdownExpanded = true
                                    }
                                    // Add debug output
                                    Log.d("SignUpScreen", "Query changed to: '$it'")
                                },
                                placeholder = "Select Clinic",
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isClinicDropdownExpanded = !isClinicDropdownExpanded
                                        // Refresh the clinic list when opening the dropdown
                                        if (isClinicDropdownExpanded) {
                                            Log.d("SignUpScreen", "Refreshing clinic list")
                                            clinicViewModel.fetchAllClinics()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = PhosphorIcons.Bold.CaretDown,
                                            contentDescription = "Expand Clinic Dropdown",
                                            tint = Primary500
                                        )
                                    }
                                }
                            )

                            androidx.compose.material3.DropdownMenu(
                                expanded = isClinicDropdownExpanded,
                                onDismissRequest = { isClinicDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(White)
                            ) {
                                if (loading) {
                                    // Show loading indicator in dropdown
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Primary500)
                                    }
                                } else if (error != null) {
                                    // Show error message with more details
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = "Error loading clinics",
                                                    color = androidx.compose.ui.graphics.Color.Red
                                                )
                                                Text(
                                                    text = error,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = androidx.compose.ui.graphics.Color.Red
                                                )
                                            }
                                        },
                                        onClick = {
                                            // Retry on click
                                            clinicViewModel.clearError()
                                            clinicViewModel.fetchAllClinics()
                                        }
                                    )
                                } else if (clinics.isEmpty()) {
                                    // Specifically check if the main clinics list is empty
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = {
                                            Text("No clinics available. Tap to refresh.")
                                        },
                                        onClick = {
                                            clinicViewModel.fetchAllClinics()
                                        }
                                    )
                                } else if (filteredClinics.isEmpty()) {
                                    // Show message when no clinics match filter
                                    androidx.compose.material3.DropdownMenuItem(
                                        text = { Text("No clinics match '${clinicQuery}'") },
                                        onClick = { /* Do nothing */ }
                                    )
                                } else {
                                    // Show list of clinics with count
                                    Text(
                                        text = "${filteredClinics.size} clinic(s) found",
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    // Show list of clinics
                                    filteredClinics.forEach { clinic ->
                                        androidx.compose.material3.DropdownMenuItem(
                                            text = { Text(clinic.name) },
                                            onClick = {
                                                clinicName = clinic.name
                                                clinicId = clinic.id
                                                clinicQuery = clinic.name
                                                isClinicDropdownExpanded = false
                                                Log.d("SignUpScreen", "Selected clinic: ${clinic.name}, ID: ${clinic.id}")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // LinkedIn Field
                        CustomTextField(
                            value = linkedin,
                            onValueChange = { linkedin = it },
                            placeholder = "LinkedIn (Optional)",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Instagram Field
                        CustomTextField(
                            value = instagram,
                            onValueChange = { instagram = it },
                            placeholder = "Instagram (Optional)",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Social Media 3 Field
                        CustomTextField(
                            value = socialMedia3,
                            onValueChange = { socialMedia3 = it },
                            placeholder = "Social Media 3 (Optional)",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Password",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm Password Field
                    CustomTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Confirm Password",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) PhosphorIcons.Bold.Eye else PhosphorIcons.Bold.EyeSlash,
                                    contentDescription = "Toggle Password Visibility",
                                    tint = Primary500
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign Up Button
                    Button(
                        onClick = {
                            val isDoctor = userType == "Doctor"

                            // Validate fields based on user type
                            val isValid = if (isDoctor) validateDoctorFields() else validateCommonFields()

                            if (isValid) {
                                isLoading = true
                                Log.d("SignUpScreen", "Submitting form - User type: $userType")

                                // Create registration request
                                val request = RegistrationRequest(
                                    username = username,
                                    email = email,
                                    password = password,
                                    password2 = confirmPassword,
                                    name = "$firstName $lastName",
                                    phone = "+213$phoneNumber",
                                    address = address,
                                    birth_date = formatDateForApi(dateOfBirth),
                                    role = if (isDoctor) "doctor" else "patient",
                                    specialty = if (isDoctor) specialty else null,
                                    clinic_id = if (isDoctor) clinicId else null
                                )

                                // For doctors, create a list of social media links to save
                                val socialMediaLinks = if (isDoctor) {
                                    listOfNotNull(
                                        if (linkedin.isNotBlank()) SocialMedia(
                                            id = 0, // API will assign actual ID
                                            doctor_id = 0, // This will be assigned by the API after doctor creation
                                            name = "LinkedIn",
                                            link = linkedin
                                        ) else null,
                                        if (instagram.isNotBlank()) SocialMedia(
                                            id = 0,
                                            doctor_id = 0,
                                            name = "Instagram",
                                            link = instagram
                                        ) else null,
                                        if (socialMedia3.isNotBlank()) SocialMedia(
                                            id = 0,
                                            doctor_id = 0,
                                            name = "Other",
                                            link = socialMedia3
                                        ) else null
                                    )
                                } else {
                                    emptyList()
                                }

                                // Register user with appropriate type
                                if (isDoctor) {
                                    authViewModel.registerDoctor(request, socialMediaLinks)
                                } else {
                                    authViewModel.registerUser(request, isDoctor = false)
                                }
                            } else {
                                val message = if (isDoctor)
                                    "Please fill all required fields including specialty and clinic"
                                else
                                    "Please fill all required fields correctly"

                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary500
                        ),
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
                                text = "Sign Up",
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

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Gray500,
                textAlign = TextAlign.Start,
                fontSize = 14.sp
            )
        },
        modifier = modifier
            .height(50.dp),
        shape = RoundedCornerShape(5.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary500,
            unfocusedBorderColor = Primary300,
            cursorColor = Primary500,
            unfocusedContainerColor = Gray100,
            focusedContainerColor = Gray100
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle = TextStyle(
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
            color = Gray900
        ),
        singleLine = true,
        trailingIcon = trailingIcon,
        readOnly = readOnly
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column {
        // Text field with calendar icon
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = modifier,

            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = PhosphorIcons.Bold.CalendarDots,
                        contentDescription = "Calendar",
                        tint = Primary500
                    )
                }
            }
        )

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                selectableDates = object : SelectableDates {
                    // Optional: Limit selectable dates (e.g., only past dates for birth date)
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis() // Only past dates
                    }
                }
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Date(millis)
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                onValueChange(formatter.format(date))
                            }
                            showDatePicker = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary500
                        )
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDatePicker = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Primary500
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    title = { Text("Select Date of Birth") }
                )
            }
        }
    }
}