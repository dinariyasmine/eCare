package com.example.patientprofile.ui.theme.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.Doctor
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import com.example.data.viewModel.DoctorViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorPersonalInfoScreen(
    navController: NavController,
    doctorRepository: DoctorRepository,
    userRepository: UserRepository
) {
    val viewModel: DoctorViewModel = viewModel(
        factory = DoctorViewModel.Factory(doctorRepository, userRepository)
    )
    val doctor by viewModel.selectedDoctor.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    data class CountryCode(
        val code: String,
        val name: String,
        val flag: String
    )

    val countryCodes = listOf(
        CountryCode("+1", "United States", "🇺🇸"),
        CountryCode("+44", "United Kingdom", "🇬🇧"),
        CountryCode("+91", "India", "🇮🇳"),
        CountryCode("+49", "Germany", "🇩🇪"),
        CountryCode("+33", "France", "🇫🇷"),
        CountryCode("+81", "Japan", "🇯🇵"),
        CountryCode("+86", "China", "🇨🇳"),
        CountryCode("+7", "Russia", "🇷🇺"),
        CountryCode("+55", "Brazil", "🇧🇷"),
        CountryCode("+61", "Australia", "🇦🇺"),
        CountryCode("+39", "Italy", "🇮🇹"),
        CountryCode("+34", "Spain", "🇪🇸"),
        CountryCode("+82", "South Korea", "🇰🇷"),
        CountryCode("+52", "Mexico", "🇲🇽"),
        CountryCode("+971", "UAE", "🇦🇪"),
        CountryCode("+966", "Saudi Arabia", "🇸🇦"),
        CountryCode("+65", "Singapore", "🇸🇬"),
        CountryCode("+31", "Netherlands", "🇳🇱"),
        CountryCode("+90", "Turkey", "🇹🇷"),
        CountryCode("+20", "Egypt", "🇪🇬")
    )
    // Personal Information
    var firstName by remember { mutableStateOf("Yasmine") }
    var lastName by remember { mutableStateOf("Dharri") }
    var email by remember { mutableStateOf("lly_dharri@esi.dz") }
    var phone by remember { mutableStateOf("(+213) 1234567899") }

    // Added date picker state variables
    var birthday by remember { mutableStateOf("18/03/2025") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Date formatter
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Extract the date components from the existing birthday string
    val initialDate = try {
        LocalDate.parse(birthday, dateFormatter)
    } catch (e: Exception) {
        LocalDate.now()
    }

    // Individual date components for the date picker
    val selectedDateState = remember { mutableStateOf(initialDate) }

    // Professional Information
    var specialty by remember { mutableStateOf("Cardiology") }
    var clinicName by remember { mutableStateOf("iGQiao Clinic") }
    var clinicsAccess by remember { mutableStateOf("Duale Smart") }
    var clinicLocation by remember { mutableStateOf("https://image.asp.gov.gl/2LwmDbXi6aq8f4j") }
    var linkedIn by remember { mutableStateOf("https://www.linkedin.com/in/dharri-yasmine/") }
    var instagram by remember { mutableStateOf("https://www.instagram.com/dharri-yasmine/") }
    var education by remember { mutableStateOf(
        "I earned my medical degree from the University of Alberta, where I specialized in internal medicine. " +
                "After completing my residency at Michigan Dental Hospital, I donated more training in cardiology to " +
                "enhance my expertise in heart-related conditions."
    ) }
    var countryCodeExpanded by remember { mutableStateOf(false) }
    // Define colors to match the styling from previous screens
    val borderColor = Color(0xFF93C5FD)
    val backgroundColor = Color(0xFFF9FAFB)
    val primaryButtonColor = Color(0xFF3B82F6)
    var selectedCountryCode by remember { mutableStateOf(countryCodes.first()) }

    var phoneWithoutCode by remember {
        mutableStateOf(
            if (phone.startsWith("+")) {
                val codeEndIndex = phone.indexOfFirst { it.isDigit().not() && it != '+' }
                if (codeEndIndex > 0) phone.substring(codeEndIndex) else phone
            } else phone
        )
    }
    val textFieldColorScheme = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = backgroundColor,
        unfocusedContainerColor = backgroundColor,
        focusedBorderColor = borderColor,
        unfocusedBorderColor = borderColor,
        focusedTextColor = Color(0xFF4A4A4A),
        unfocusedTextColor = Color(0xFF4A4A4A),
        focusedLabelColor = Color(0xFF6E6E6E),
        unfocusedLabelColor = Color(0xFF6E6E6E)
    )

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error!!, color = Color.Red)
        }
        return
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthday = selectedDateState.value.format(dateFormatter)
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDateState.value.toEpochDay() * 24 * 60 * 60 * 1000
                ),
                title = { Text("Select Date") },
                headline = { Text("Select your birthday") },
                showModeToggle = true,

            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Doctor Profile",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Picture
        Image(
            painter = rememberAsyncImagePainter("https://example.com/doctor-profile.jpg"),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Information Section
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start),
            color = Color(0xFF4A4A4A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // First Name Field
        Text(
            text = "First Name",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Last Name Field
        Text(
            text = "Last Name",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Field
        Text(
            text = "Email",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Field
        Text(
            text = "Phone Number",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Country code selector
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { countryCodeExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedCountryCode.flag} ${selectedCountryCode.code}",
                        color = Color(0xFF4A4A4A)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Select Country Code",
                        tint = Color(0xFF6E6E6E)
                    )
                }

                // Country code dropdown
                if (countryCodeExpanded) {
                    Dialog(onDismissRequest = { countryCodeExpanded = false }) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 8.dp
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .heightIn(max = 300.dp)
                            ) {
                                items(countryCodes) { countryCode ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedCountryCode = countryCode
                                                countryCodeExpanded = false
                                            }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${countryCode.flag} ${countryCode.name}",
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(text = countryCode.code)
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Phone number input field
            OutlinedTextField(
                value = phoneWithoutCode,
                onValueChange = {
                    phoneWithoutCode = it
                    phone = "${selectedCountryCode.code}$phoneWithoutCode"
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColorScheme
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.height(8.dp))

        // Birthday Field - Modified to show date picker
        Text(
            text = "Birthday",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = birthday,
            onValueChange = { /* Read-only field */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp),
            readOnly = true,  // Make it read-only since we're using the date picker
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Select date"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Professional Information Section
        Text(
            text = "Professional Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start),
            color = Color(0xFF4A4A4A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Specialty Field
        Text(
            text = "Specialty",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = specialty,
            onValueChange = { specialty = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Clinic Name Field
        Text(
            text = "Clinic Name",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = clinicName,
            onValueChange = { clinicName = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Clinics Access Field
        Text(
            text = "Clinics Access",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = clinicsAccess,
            onValueChange = { clinicsAccess = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Clinic Location Field
        Text(
            text = "Clinics Maps Location",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = clinicLocation,
            onValueChange = { clinicLocation = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // LinkedIn Field
        Text(
            text = "LinkedIn Link",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = linkedIn,
            onValueChange = { linkedIn = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Instagram Field
        Text(
            text = "Instagram Link",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = instagram,
            onValueChange = { instagram = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Education Section
        Text(
            text = "Education",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start),
            color = Color(0xFF4A4A4A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Education Field
        Text(
            text = "Education & Training",
            color = Color(0xFF6E6E6E),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = education,
            onValueChange = { education = it },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColorScheme,
            shape = RoundedCornerShape(12.dp),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button with less rounded corners (matching previous request)
        Button(
            onClick = {
                // Handle save action
                val updatedDoctor = doctor?.copy(
                    // Update doctor fields here
                )
                updatedDoctor?.let { viewModel.updateDoctor(it) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(4.dp), // Less rounded corners
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryButtonColor,
                contentColor = Color.White
            )
        ) {
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}