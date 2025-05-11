/*package com.example.patientprofile.ui.theme.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.data.repository.UserRepository
import com.example.data.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

data class CountryyCode(
    val code: String,
    val name: String,
    val flag: String,
    val s: String,

) {
    val flagUrl:String=""
    val phoneCode: String = ""
}

val countryCodes = listOf(
    CountryCode(
        "+1", "United States", "🇺🇸", "+1",

    ),
    CountryCode("+44", "United Kingdom", "🇬🇧", "+1"),
    CountryCode("+91", "India", "🇮🇳", "+1"),
    CountryCode("+49", "Germany", "🇩🇪", "+1"),
    CountryCode("+33", "France", "🇫🇷", "+1"),
    CountryCode("+81", "Japan", "🇯🇵", "+1"),
    CountryCode("+86", "China", "🇨🇳", "+1"),
    CountryCode("+7", "Russia", "🇷🇺", "+1"),
    CountryCode("+55", "Brazil", "🇧🇷", "+1"),
    CountryCode("+61", "Australia", "🇦🇺", "+1"),
    CountryCode("+39", "Italy", "🇮🇹", "+1"),
    CountryCode("+34", "Spain", "🇪🇸", "+1"),
    CountryCode("+82", "South Korea", "🇰🇷", "+1"),
    CountryCode("+52", "Mexico", "🇲🇽", "+1"),
    CountryCode("+971", "UAE", "🇦🇪", "+1"),
    CountryCode("+966", "Saudi Arabia", "🇸🇦", "+1"),
    CountryCode("+65", "Singapore", "🇸🇬", "+1"),
    CountryCode("+31", "Netherlands", "🇳🇱", "+1"),
    CountryCode("+90", "Turkey", "🇹🇷", "+1"),
    CountryCode("+20", "Egypt", "🇪🇬", "+1")
)

class CountryCode(s: String, s1: String, s2: String, s3: String) {

}

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
    val scrollState = rememberScrollState()

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

        // Country code selection
        var countryCodeExpanded by remember { mutableStateOf(false) }
        var selectedCountryCode by remember { mutableStateOf(countryCodes.first()) }
        var phoneWithoutCode by remember {
            mutableStateOf(
                if (phone.startsWith("+")) {
                    val codeEndIndex = phone.indexOfFirst { it.isDigit().not() && it != '+' }
                    if (codeEndIndex > 0) phone.substring(codeEndIndex) else phone
                } else phone
            )
        }

        val calendar = Calendar.getInstance()
        try {
            userData.birth_date?.let { calendar.time = it }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
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

            // First Name Field
            Text(
                text = "First Name",
                color = Color(0xFF6E6E6E),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 4.dp)
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
                modifier = Modifier.padding(bottom = 4.dp)
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
                modifier = Modifier.padding(bottom = 4.dp)
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

            // Birth Date Field
            Text(
                text = "Birth Date",
                color = Color(0xFF6E6E6E),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = birthday,
                onValueChange = { },
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
                colors = textFieldColorScheme,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phone Number Field with Country Code
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
                            text = "${selectedCountryCode.flagUrl} ${selectedCountryCode.code}",
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
                                                text = "${countryCode.flagUrl} ${countryCode.name}",
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

            // Address Field
            Text(
                text = "Address",
                color = Color(0xFF6E6E6E),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColorScheme,
                shape = RoundedCornerShape(12.dp)
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
                        phone = "${selectedCountryCode.code}$phoneWithoutCode",
                        adress = address,
                        birth_date = parsedBirthDate ?: userData.birth_date
                    )
                    // viewModel.updateUser(updatedUser)
                },
                modifier = Modifier.fillMaxWidth(),
                // Changed from the default button shape to a custom shape with smaller corner radius
                shape = RoundedCornerShape(6.dp) ,// Reduced corner radius for less rounding
                colors = ButtonDefaults.buttonColors(
                    // Changed from the default button shape to a custom shape with smaller corner radius
                    // Reduced corner radius for less rounding
                    containerColor = primaryButtonColor,
                    contentColor = Color.White,
                    // Changed from the default button shape to a custom shape with smaller corner radius
                  //  shape = RoundedCornerShape(6.dp) // Reduced corner radius for less rounding
                )
            ) {
                Text("Save")
            }

            // Add extra space at bottom for scrolling
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}*/


