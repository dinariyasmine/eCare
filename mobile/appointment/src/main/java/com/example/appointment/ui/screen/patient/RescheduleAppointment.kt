
package com.example.appointment.ui.screen.patient

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.example.appointment.ui.screen.components.appoint.DatePicker
import com.example.appointment.ui.screen.components.appoint.PatientFormState
import com.example.appointment.ui.screen.components.appoint.TimeSlotPicker
import com.example.core.theme.ECareMobileTheme
import com.example.data.model.AppointmentRequest
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescheduleAppointmentScreen(
    viewModel: AppointmentViewModel,
    availabilityViewModel: AvailabilityViewModel,
    navController: NavHostController,
    appointmentId: String
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Fetch the specific appointment
    LaunchedEffect(appointmentId) {
        viewModel.getAppointmentById(appointmentId.toInt())
        availabilityViewModel.availabilities // Load availabilities
    }

    // Observe the appointment details
    val appointment by viewModel.currentAppointment.observeAsState()

    // State for selected date and time slot
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val selectedSlot = remember { mutableStateOf<String?>(null) }

    // Error handling
    val error by viewModel.error.observeAsState()
    val availabilityError by availabilityViewModel.error.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(availabilityError) {
        availabilityError?.let {
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
                title = { Text("Reschedule Appointment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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

            appointment?.let { appt ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Original appointment details
                    Text(
                        text = "Original Appointment",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Date:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = appt.start_time.toLocalDate().toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Time:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "${appt.start_time.toLocalTime()} - ${appt.end_time.toLocalTime()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // New appointment selection
                    Text(
                        text = "Select New Time Slot",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    DatePicker(
                        selectedDate = selectedDate.value,
                        onDateSelected = { date ->
                            selectedDate.value = date
                            selectedSlot.value = null // Reset time slot when date changes
                        }
                    )

                    TimeSlotPicker(
                        selectedDate = selectedDate.value,
                        selectedSlot = selectedSlot.value,
                        doctorId = appt.doctor_id,
                        onSlotSelected = { slot ->
                            selectedSlot.value = slot
                        },
                        availabilityViewModel = availabilityViewModel
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Reschedule button
                    Button(
                        onClick = {
                            selectedSlot.value?.let { slot ->
                                // Parse the selected time slot (format: "hh:mm a")
                                val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
                                val startTime = LocalTime.parse(slot, timeFormatter)
                                val endTime = startTime.plusMinutes(30) // Assuming 30-minute slots

                                val newStartDateTime = LocalDateTime.of(
                                    selectedDate.value,
                                    startTime
                                )
                                val newEndDateTime = LocalDateTime.of(
                                    selectedDate.value,
                                    endTime
                                )
                                // Create AppointmentRequest from existing appointment
                                val appointmentRequest = AppointmentRequest(
                                    doctor = appointment!!.doctor_id,
                                    patient = appointment!!.patient_id,
                                    start_time = newStartDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                                    end_time = newEndDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
                                    name = appointment!!.name,
                                    gender = appointment!!.gender,
                                    age = appointment!!.age,
                                    problem_description = appointment!!.problem_description
                                )

                                // Update the appointment
                                viewModel.updateAppointment(
                                    id = appointment!!.id,
                                    appointment = appointmentRequest
                                )

                                // Show success message and navigate back
                                Toast.makeText(
                                    context,
                                    "Appointment rescheduled successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } ?: run {
                                Toast.makeText(
                                    context,
                                    "Please select a time slot",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            contentColor = Color.White,
                        ),
                        enabled = selectedSlot.value != null
                    ) {
                        Text(
                            "Confirm Reschedule",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } ?: run {
                // Loading or error state
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (error != null) {
                        Text("Failed to load appointment details")
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}