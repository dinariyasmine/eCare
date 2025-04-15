package com.example.patientprofile.ui.theme.screens
import androidx.compose.material3.OutlinedTextField

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data.repository.UserRepository
import com.example.data.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PersonalInfoScreen(
    navController: NavController,
    userRepository: UserRepository
) {
    val viewModel: UserViewModel = viewModel(factory = UserViewModel.Factory(userRepository))
    val user by viewModel.selectedUser.collectAsState()
    val context = LocalContext.current
    val locale = Locale.getDefault()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", locale)

    // Fetch user when screen is shown
    LaunchedEffect(Unit) {
        viewModel.getUserById(1)
    }

    user?.let { userData ->
        val nameParts = userData.name.split(" ", limit = 2)
        val initialFirstName = nameParts.firstOrNull() ?: ""
        val initialLastName = if (nameParts.size > 1) nameParts[1] else ""

        var firstName by remember { mutableStateOf(initialFirstName) }
        var lastName by remember { mutableStateOf(initialLastName) }
        var email by remember { mutableStateOf(userData.email) }
        var phone by remember { mutableStateOf(userData.phone) }
        var address by remember { mutableStateOf(userData.adress ?: "") }

        var birthday by remember {
            mutableStateOf(userData.birth_date?.let { dateFormat.format(it) } ?: "")
        }

        val calendar = Calendar.getInstance()

        try {
            userData.birth_date?.let {
                calendar.time = it
            }
        } catch (_: Exception) {}

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                birthday = dateFormat.format(calendar.time)
            },
            year,
            month,
            day
        )

        val borderColor = Color(0xFF93C5FD)
        val backgroundColor = Color(0xFFF9FAFB)
        val primaryButtonColor = Color(0xFF3B82F6)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = rememberAsyncImagePainter(userData.password),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
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

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor,
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = birthday,
                onValueChange = { },
                label = { Text("Birth Date") },
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            tint = primaryButtonColor
                        )
                    }
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                keyboardActions = KeyboardActions.Default,
                visualTransformation = VisualTransformation.None,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor,
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor,
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val parsedBirthDate = try {
                        dateFormat.parse(birthday)
                    } catch (e: Exception) {
                        null
                    }

                    val updatedUser = userData.copy(
                        name = "$firstName $lastName",
                        email = email,
                        phone = phone,
                        adress = address,
                        birth_date = parsedBirthDate ?: userData.birth_date
                    )
                    // viewModel.updateUser(updatedUser)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryButtonColor,
                    contentColor = Color.White
                )
            ) {
                Text("Save")
            }
        }
    }
}
