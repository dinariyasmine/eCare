@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appointment.ui.screen.patient

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appointment.ui.screen.components.appoint.DatePicker
import com.example.appointment.ui.screen.components.appoint.TimeSlotPicker
import com.example.core.theme.ECareMobileTheme
import com.example.data.model.Appointment
import com.example.data.repository.AvailabilityRepository
import com.example.data.retrofit.AvailabilityEndpoint
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RescheduleAppointmentScreen(
    appointment: Appointment,
    onBack: () -> Unit,
    viewModel: AppointmentViewModel,
    availabilityViewModel: AvailabilityViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Date and Time states initialized with existing appointment values
    val selectedDate = remember {
        mutableStateOf(appointment.date ?: LocalDate.now())
    }

    val selectedSlot = remember {
        mutableStateOf(appointment.start_time ?: LocalTime.now())
    }

    val formData = remember {
        mutableStateOf(
            PatientFormState(
                fullName = appointment.name ,
                gender = appointment.gender,
                age = appointment.age,
                problemDescription = appointment.problem_description
            )
        )
    }

    // Error and success handling
    val error by viewModel.error.observeAsState()
    val success by viewModel.operationSuccess.observeAsState()

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(success) {
        if (success == true) {
            Toast.makeText(context, "Appointment rescheduled successfully!", Toast.LENGTH_SHORT).show()
            onBack()
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
                    IconButton(onClick = onBack) {
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

                Text("Available Time", fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 16.dp))
                val date = Date.from(selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant())

                TimeSlotPicker(
                    selectedDate = date,
                    selectedSlot = selectedSlot.value.format(DateTimeFormatter.ofPattern("HH:mm")),
                    doctorId = appointment.doctor_id ?: 101,
                    onSlotSelected = { slot ->
                        selectedSlot.value = LocalTime.parse(slot)
                    },
                    availabilityViewModel= availabilityViewModel
                )
            }

            Text(
                "Patient Details",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )


            Button(
                onClick = {
                    val updatedAppointment = appointment.copy(
                        date = selectedDate.value,
                        start_time = selectedSlot.value,
                        end_time = selectedSlot.value.plusMinutes(30),
                        name = formData.value.fullName,
                        gender = formData.value.gender,
                        age = formData.value.age,
                        problem_description = formData.value.problemDescription
                    )
                    viewModel.updateAppointment(appointment.id, updatedAppointment)
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
                    "Reschedule Appointment",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}