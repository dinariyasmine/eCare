package com.example.patientprofile.ui.theme.screens

import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.Doctor
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import com.example.data.viewModel.DoctorViewModel

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

    // Personal Information
    var firstName by remember { mutableStateOf("Yasmine") }
    var lastName by remember { mutableStateOf("Dharri") }
    var email by remember { mutableStateOf("lly_dharri@esi.dz") }
    var phone by remember { mutableStateOf("(+213) 1234567899") }
    var birthday by remember { mutableStateOf("18/03/2025") }

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

    val borderColor = Color(0xFF93C5FD)
    val backgroundColor = Color(0xFFF9FAFB)
    val primaryButtonColor = Color(0xFF3B82F6)

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
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Personal Information Section
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        DoctorInfoField(
            label = "First Name",
            value = firstName,
            onValueChange = { firstName = it }
        )


        DoctorInfoField(
            label = "First Name",
            value = firstName,
            onValueChange = { firstName = it }
        )

        DoctorInfoField(
            label = "Last Name",
            value = lastName,
            onValueChange = { lastName = it }
        )

        DoctorInfoField(
            label = "Email",
            value = email,
            onValueChange = { email = it }
        )

        DoctorInfoField(
            label = "Phone Number",
            value = phone,
            onValueChange = { phone = it }
        )

        DoctorInfoField(
            label = "Birthday",
            value = birthday,
            onValueChange = { birthday = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Professional Information Section
        Text(
            text = "Professional Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = specialty,
            onValueChange = { specialty = it },
            label = { Text("Specialty") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

// Clinic Name Field
        OutlinedTextField(
            value = clinicName,
            onValueChange = { clinicName = it },
            label = { Text("Clinic Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

// Clinics Access Field
        OutlinedTextField(
            value = clinicsAccess,
            onValueChange = { clinicsAccess = it },
            label = { Text("Clinics Access") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

// Clinic Location Field
        OutlinedTextField(
            value = clinicLocation,
            onValueChange = { clinicLocation = it },
            label = { Text("Clinics Maps Location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

// LinkedIn Field
        OutlinedTextField(
            value = linkedIn,
            onValueChange = { linkedIn = it },
            label = { Text("LinkedIn Link") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

// Instagram Field
        OutlinedTextField(
            value = instagram,
            onValueChange = { instagram = it },
            label = { Text("Instagram Link") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Education Section
        Text(
            text = "Education",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = education,
            onValueChange = { education = it },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor
            ),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
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

@Composable
fun DoctorInfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = Color(0xFF93C5FD)
    val backgroundColor = Color(0xFFF9FAFB)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor
        )
    )
}