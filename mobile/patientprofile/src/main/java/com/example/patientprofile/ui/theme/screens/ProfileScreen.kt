package com.example.patientprofile.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Pill
import com.example.data.repository.UserRepository
import com.example.data.viewModel.UserViewModel
import com.example.patientprofile.ui.theme.screens.PersonalInfoScreen
import com.example.patientprofile.R
import com.example.patientprofile.ui.theme.components.ProfileHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalInfoTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
   // keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color(0xFF666E7A),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF90CAF9),
                unfocusedBorderColor = Color(0xFFD1DBEA),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF8F9FC)
            ),
           // keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            leadingIcon = leadingIcon,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF333333)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorPersonalInfoScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("Yasmine") }
    var lastName by remember { mutableStateOf("Dinari") }
    var email by remember { mutableStateOf("ly_dinari@esi.dz") }
    var phoneNumber by remember { mutableStateOf("(+213) 123456789") }
    var address by remember { mutableStateOf("Oued Smar, Alger, Algeria") }
    var birthdate by remember { mutableStateOf("18/03/2004") }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Informations") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile image - removed as per the new design focus on the fields

            Spacer(modifier = Modifier.height(16.dp))

            // Form fields with updated style
            PersonalInfoTextField(
                label = "First Name",
                value = firstName,
                onValueChange = { firstName = it }
            )

            PersonalInfoTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
              //  keyboardType = KeyboardType.Email
            )

            PersonalInfoTextField(
                label = "Last Name",
                value = lastName,
                onValueChange = { lastName = it }
            )

            // Birthday field with custom design
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Birthday",
                    color = Color(0xFF666E7A),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = birthdate,
                    onValueChange = { /* Handled by date picker */ },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF90CAF9),
                        unfocusedBorderColor = Color(0xFFD1DBEA),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF8F9FC)
                    ),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "Select date",
                                tint = Color(0xFF90CAF9)
                            )
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF333333)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            PersonalInfoTextField(
                label = "Phone Number",
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
               // keyboardType = KeyboardType.Phone,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Algeria Flag",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                }
            )

            PersonalInfoTextField(
                label = "Address",
                value = address,
                onValueChange = { address = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("Save", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Date picker dialog

}}