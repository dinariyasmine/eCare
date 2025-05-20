@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appointment.ui.screen.patient

import PatientForm
import PatientFormState
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.appointment.ui.screen.components.appoint.DatePicker
import com.example.appointment.ui.screen.components.appoint.TimeSlotPicker
import com.example.core.theme.ECareMobileTheme
import com.example.data.model.AppointmentRequest
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewAppointmentScreen(viewModel: AppointmentViewModel, availabilityViewModel: AvailabilityViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val selectedSlot = remember { mutableStateOf<String?>(null) }
    var formData = remember { mutableStateOf<PatientFormState?>(null) }

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
                    IconButton(onClick = {

                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            modifier = Modifier.border(width = 1.dp,
                                color = Color(0xFF222B45), shape = RectangleShape))
                    }
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                DatePicker(
                    selectedDate = selectedDate.value,
                    onDateSelected = { newDate -> selectedDate.value = newDate }
                )

                Text("Available Time", fontWeight = FontWeight.Medium)
                val date = Date.from(selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant())

                TimeSlotPicker(
                    selectedDate = date,
                    selectedSlot = selectedSlot.value,
                    doctorId = 11,
                    onSlotSelected = { slot -> selectedSlot.value = slot },
                    availabilityViewModel = availabilityViewModel,
                )
            }

            Text("Patient Details", fontWeight = FontWeight.Medium)
            PatientForm(
                onFormSubmit = { submittedData ->
                    formData.value = submittedData
                }
            )

            Button(
                onClick = {
                    // Validation
                    if (selectedSlot.value == null) {
                        Toast.makeText(context, "Please select a time slot", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    formData.value?.let { data ->
                        // Parse time
                        val timeParts = selectedSlot.value!!.split(":")
                        val startTime = LocalTime.of(timeParts[0].toInt(), timeParts[1].toInt())

                        // Create request
                        val request = AppointmentRequest(
                            doctor_id = 11,
                            patient_id = 1,
                            date = selectedDate.value,
                            start_time = startTime,
                            end_time = startTime.plusMinutes(30),
                            name = data.fullName,
                            gender = data.gender,
                            age = data.age,
                            problem_description = data.problemDescription
                        )

                        viewModel.createAppointment(request)
                        Toast.makeText(context, "Appointment created!", Toast.LENGTH_SHORT).show()
                    } ?: run {
                        Toast.makeText(context, "Please fill all patient details", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
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