package com.example.patientprofile.ui.theme.screens

import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import com.example.data.viewModel.DoctorViewModel
import java.io.Serializable

@Composable
fun DoctorPersonalInfoScreen(
    navController: NavController,


) {
    // Initialize the ViewModel directly without a factory
    val doctorViewModel: DoctorViewModel = viewModel()

    val doctor by doctorViewModel.doctorDetails.collectAsState()
    val loading by doctorViewModel.loading.collectAsState()
    val error by doctorViewModel.error.collectAsState()

    // Initialize state variables with data from ViewModel
    val firstName = remember { mutableStateOf(doctor?.user?.name ?: "") }
   // val lastName = remember { mutableStateOf(doctor?.lastName ?: "") }
    val email = remember { mutableStateOf(doctor?.user?.email ?: "") }
    val phone = remember { mutableStateOf(doctor?.user?.phone ?: "") }
    var birthday by remember { mutableStateOf("18/03/2025") }

// Assign like this:


    // Professional Information
    val specialty = remember { mutableStateOf(doctor?.doctor?.specialty ?: "") }
    val clinicName = remember { mutableStateOf(doctor?.clinic?.name ?: "") }
  //  val clinicsAccess = remember { mutableStateOf(doctor?.clinicsAccess ?: "") }
    val clinicLocation = remember { mutableStateOf(doctor?.clinic?.map_location ?: "") }
    val linkedIn = remember { mutableStateOf("linkedin") }
    val instagram = remember { mutableStateOf("doctor?.instagram ?: ") }
    val education = remember { mutableStateOf("doctor?.education ?: " )}

    // Update state when doctor changes
    LaunchedEffect(doctor) {
        doctor?.let { doc ->
            firstName.value = doc.user.name ?: ""
           // lastName.value = doc.lastName ?: ""
            email.value = doc.user.email ?: ""
            phone.value = doc.user.phone ?: ""
            birthday = doc.user.birth_date.toString() ?: "18/03/2025"
            specialty.value = doc.doctor.specialty ?: ""
            clinicName.value = doc.clinic?.name ?: ""
            //clinicsAccess.value = doc.clinicsAccess ?: ""
            clinicLocation.value = doc.clinic?.map_location ?: ""
            linkedIn.value = "rr"
            instagram.value = "in"
            education.value = doc.doctor.description?: ""
        }
    }

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
        doctor?.doctor?.photo?.let { picUrl ->
            Image(
                painter = rememberAsyncImagePainter(picUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        } ?: run {
            // Default image or placeholder when no profile picture is available
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image")
            }
        }

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
            value = firstName.value,
            onValueChange = { firstName.value = it }
        )

//        DoctorInfoField(
//            label = "Last Name",
//            value = lastName.value,
//            onValueChange = { lastName.value = it }
//        )

        DoctorInfoField(
            label = "Email",
            value = email.value,
            onValueChange = { email.value = it }
        )

        DoctorInfoField(
            label = "Phone Number",
            value = phone.value,
            onValueChange = { phone.value = it }
        )

//        DoctorInfoField(
//            label = "Birthday",
//            value = birthday.value.toString(),
//            onValueChange = { birthday.value = it }
//        )

        Spacer(modifier = Modifier.height(24.dp))

        // Professional Information Section
        Text(
            text = "Professional Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = specialty.value,
            onValueChange = { specialty.value = it },
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
            value = clinicName.value,
            onValueChange = { clinicName.value = it },
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
//        OutlinedTextField(
//            value = clinicsAccess.value,
//            onValueChange = { clinicsAccess.value = it },
//            label = { Text("Clinics Access") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true,
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedContainerColor = backgroundColor,
//                unfocusedContainerColor = backgroundColor,
//                focusedBorderColor = borderColor,
//                unfocusedBorderColor = borderColor
//            )
//        )

        Spacer(modifier = Modifier.height(8.dp))

        // Clinic Location Field
        OutlinedTextField(
            value = clinicLocation.value,
            onValueChange = { clinicLocation.value = it },
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
            value = linkedIn.value,
            onValueChange = { linkedIn.value = it },
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
            value = instagram.value,
            onValueChange = { instagram.value = it },
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
            value = education.value,
            onValueChange = { education.value = it },
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
                // Update doctor object with current field values
//                doctor?.let { currentDoctor ->
//                    val updatedDoctor = currentDoctor.copy(
//                        firstName = firstName.value,
//                        lastName = lastName.value,
//                        email = email.value,
//                        phone = phone.value,
//                        birthday = birthday.value,
//                        specialty = specialty.value,
//                        clinicName = clinicName.value,
//                        clinicsAccess = clinicsAccess.value,
//                        clinicLocation = clinicLocation.value,
//                        linkedIn = linkedIn.value,
//                        instagram = instagram.value,
//                        education = education.value
//                    )
//                    doctorViewModel.updateDoctor(updatedDoctor)
//                }
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