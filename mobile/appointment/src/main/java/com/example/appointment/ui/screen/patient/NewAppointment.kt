@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appointment.ui.screen.patient

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import com.example.appointment.ui.screen.components.appoint.DatePicker
import com.example.appointment.ui.screen.components.appoint.PatientForm
import com.example.appointment.ui.screen.components.appoint.PatientFormState
import com.example.appointment.ui.screen.components.appoint.TimeSlotPicker
import com.example.core.theme.ECareMobileTheme
import com.example.data.model.AppointmentRequest
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewAppointmentScreen(
    viewModel: AppointmentViewModel,
    availabilityViewModel: AvailabilityViewModel,
    //onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val selectedSlot = remember { mutableStateOf<String?>(null) }
    val formState = remember { mutableStateOf(PatientFormState()) }

    // Error handling
    val error by viewModel.error.observeAsState()
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    ECareMobileTheme {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("New Appointment") },
                navigationIcon = {
                    IconButton(onClick = {} //onBackClick
                     ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.border(
                                width = 1.dp,
                                color = Color(0xFF222B45),
                                shape = RectangleShape
                            )
                        )
                    }
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                DatePicker(
                    selectedDate = selectedDate.value,
                    onDateSelected = { newDate -> selectedDate.value = newDate }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Available Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                val date = Date.from(selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant())
                TimeSlotPicker(
                    selectedDate = selectedDate.value,
                    selectedSlot = selectedSlot.value,
                    doctorId = 11,
                    onSlotSelected = { slot -> selectedSlot.value = slot },
                    availabilityViewModel = availabilityViewModel,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Patient Details",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            PatientForm(
                formState = formState.value,
                onValueChange = { newState -> formState.value = newState }
            )

            Button(
                onClick = {
                    if (selectedSlot.value == null) {
                        Toast.makeText(context, "Please select a time slot", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    with(formState.value) {
                        if (fullName.isBlank() || age.isBlank() || gender.isBlank() || problemDescription.isBlank()) {
                            Toast.makeText(context, "Please fill all patient details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Create formatter for "hh:mm a" format (e.g., "09:00 AM")
                        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

                        // Parse the time
                        val parsedTime = LocalTime.parse(selectedSlot.value!!, timeFormatter)
                        val startDateTime = selectedDate.value.atTime(parsedTime)
                        val endDateTime = startDateTime.plusMinutes(30)

                        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        val zoneId = ZoneId.systemDefault()

                        val startZoned = startDateTime.atZone(zoneId)
                        val endZoned = endDateTime.atZone(zoneId)

                        val request = AppointmentRequest(
                            doctor = 11,
                            patient = 1,
                            start_time = startZoned.format(formatter),
                            end_time = endZoned.format(formatter),
                            name = formState.value.fullName,
                            gender = formState.value.gender,
                            age = formState.value.age,
                            problem_description = formState.value.problemDescription
                        )
                        println("Request: $request")

                        viewModel.createAppointment(request)
                        Toast.makeText(context, "Appointment created!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Set the Appointment",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}