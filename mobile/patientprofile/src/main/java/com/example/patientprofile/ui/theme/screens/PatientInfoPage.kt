package com.example.patientprofile.ui.theme.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.network.UpdatePatientRequest
import com.example.data.viewModel.PatientViewModel
import com.example.patientprofile.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CountryCode(
    val name: String,
    val code: String,
    val flagResId: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    patientId: Int,
    onBackClick: () -> Unit = {},
    viewModel: PatientViewModel = viewModel()
) {
    val patient by viewModel.selectedPatient.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load data on first render
    LaunchedEffect(patientId) {
        viewModel.loadPatientById(patientId)
    }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    // For date picker
    var showDatePicker by remember { mutableStateOf(false) }

    // For country code selection
    var showCountryCodeDialog by remember { mutableStateOf(false) }
    var selectedCountryCode by remember { mutableStateOf(CountryCode("Algeria", "+213", R.drawable.japan)) }

    // Sample list of country codes
    val countryCodes = remember {
        listOf(
            CountryCode("Algeria", "+213", R.drawable.japan),
            CountryCode("Algeria", "+213", R.drawable.japan),
            CountryCode("Algeria", "+213", R.drawable.japan),
            CountryCode("Algeria", "+213", R.drawable.japan),
//            CountryCode("Morocco", "+212", R.drawable.flag_morocco),
//            CountryCode("Tunisia", "+216", R.drawable.flag_tunisia),
//            CountryCode("France", "+33", R.drawable.flag_france),
//            CountryCode("United States", "+1", R.drawable.flag_usa),
//            CountryCode("United Kingdom", "+44", R.drawable.flag_uk)
        )
    }

    LaunchedEffect(patient) {
        patient?.let {
            firstName = it.name.split(" ").firstOrNull() ?: ""
            lastName = it.name.split(" ").drop(1).joinToString(" ")
            email = it.email
            phone = it.phone
            address = it.address
            birthDate = it.birth_date ?: ""
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        birthDate = localDate.format(DateTimeFormatter.ISO_DATE)
                    }
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
            DatePicker(state = datePickerState)
        }
    }

    // Country code selection dialog
    if (showCountryCodeDialog) {
        AlertDialog(
            onDismissRequest = { showCountryCodeDialog = false },
            title = { Text("Select Country Code") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    countryCodes.forEach { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCountryCode = country
                                    showCountryCodeDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = country.flagResId),
                                contentDescription = country.name,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "${country.name} (${country.code})")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Personal Informations",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)

                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White // Set the background to white
    ) { paddingValues ->
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Profile Image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF93C5FD))
                ) {
                    // Replace with actual image resource
                    // For now using a placeholder icon
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        tint = Color.White
                    )

                    // Uncomment and replace with appropriate resource when available
                    /*
                    Image(
                        painter = painterResource(id = R.drawable.profile_photo),
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    */
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Form fields
                FormField(
                    label = "First Name",
                    value = firstName,
                    onValueChange = { firstName = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormField(
                    label = "Last Name",
                    value = lastName,
                    onValueChange = { lastName = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Birthday field with calendar icon and date picker
                FormField(
                    label = "Birthday",
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date",
                            tint = Color.Gray,
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phone field with country code
                Column(modifier = Modifier.fillMaxWidth()  .padding(horizontal = 18.dp)) {
                    Text(
                        "Phone Number",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFF93C5FD),
                            focusedContainerColor = Color(0xFFF9FAFB),
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                            focusedBorderColor = Color(0xFF2196F3)
                        ),
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { showCountryCodeDialog = true }
                                    .padding(start = 8.dp, end = 8.dp)
                            ) {
                                // Country flag
                                Image(
                                    painter = painterResource(id = selectedCountryCode.flagResId),
                                    contentDescription = "Country flag",
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // Country code
                                Text(
                                    "(${selectedCountryCode.code})",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select country code"
                                )
                            }
                        },
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormField(
                    label = "Address",
                    value = address,
                    onValueChange = { address = it }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Save Button
                Button(
                    onClick = {
                        val fullName = "$firstName $lastName".trim()
                        val updatedFields = UpdatePatientRequest(
                            name = fullName,
                            email = email,
                            phone = phone,
                            address = address,
                            birth_date = birthDate
                        )
                        Log.d("PatientProfileScreen", "Updated fields: $updatedFields")
                        viewModel.updatePatientOnServer(patientId, updatedFields)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .height(48.dp)  .padding(horizontal = 18.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3) // Bright blue color as seen in the image
                    )
                ) {
                    Text(
                        "Save",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                error?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = Color.Red)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()  .padding(horizontal = 18.dp),) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),


           shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFF93C5FD),
                focusedContainerColor = Color(0xFFF9FAFB),
                unfocusedContainerColor = Color(0xFFF9FAFB),
                focusedBorderColor = Color(0xFF2196F3)
            ),
            trailingIcon = trailingIcon,
            singleLine = true
        )
    }
}