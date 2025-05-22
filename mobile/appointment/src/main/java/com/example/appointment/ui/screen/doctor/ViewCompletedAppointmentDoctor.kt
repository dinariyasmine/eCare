@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appointment.ui.screen.doctor

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavHostController
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.FilePdf
import com.example.appointment.ui.screen.components.appoint.DatePicker
import com.example.appointment.ui.screen.components.appoint.PatientForm
import com.example.appointment.ui.screen.components.appoint.PatientFormState
import com.example.appointment.ui.screen.components.appoint.TimeSlotPicker
import com.example.core.theme.ECareMobileTheme
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Composable
fun ViewCompletedAppointmentDoctorScreen(
    viewModel: AppointmentViewModel,
    availabilityViewModel: AvailabilityViewModel,
    navController: NavHostController,
    appointmentId: Int
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Fetch the specific appointment
    LaunchedEffect(appointmentId) {
        viewModel.getAppointmentById(appointmentId)
    }

    // Observe the appointment details
    val appointment by viewModel.currentAppointment.observeAsState()

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
                .padding(10.dp)
        ) {
            TopAppBar(
                title = { Text("Appointment Completed") },
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
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { /* Disabled */ }
                ) {
                    // Prescription section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable {
                                //handles writing prescription
                            }
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Bold.FilePdf,
                            contentDescription = null,
                            modifier = Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(5.dp)),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Prescription",
                            color = Color(0xFF4B5563),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Appointment date (read-only)
                        Text(
                            text = "Appointment Date",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "${appt.start_time.toLocalDate()}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Appointment time (read-only)
                        Text(
                            text = "Appointment Time",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "${appt.start_time.toLocalTime()} - ${appt.end_time.toLocalTime()}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Patient information
                        Text(
                            text = "Patient Information",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = appt.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Age: ${appt.age} | Gender: ${appt.gender}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Patient details (read-only)
                    Text(
                        text = "Patient Details",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    PatientForm(
                        formState = PatientFormState(
                            fullName = appt.name,
                            age = appt.age,
                            gender = appt.gender,
                            problemDescription = appt.problem_description
                        ),
                        onValueChange = { /* Disabled - no action */ },
                        modifier = Modifier.alpha(0.6f)
                    )
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