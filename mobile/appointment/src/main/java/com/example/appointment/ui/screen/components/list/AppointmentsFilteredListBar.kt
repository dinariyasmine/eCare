package com.example.appointment.ui.screen.components.list

import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.QrCode
import com.adamglin.phosphoricons.bold.FilePdf
import com.adamglin.phosphoricons.light.CalendarCheck
import com.adamglin.phosphoricons.light.Clock
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adamglin.phosphoricons.Light
import com.example.appointment.R
import com.example.data.viewModel.AppointmentViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AppointmentsFilteredListBar(
    patientId: Int,
    viewModel: AppointmentViewModel,
    onReschedule: (Appointment) -> Unit,
    onViewCompleted: (Int) -> Unit,
    onViewConfirmed: (Int) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Current") }
    val interactionSource = remember { MutableInteractionSource() }

    // State for appointments
    val allAppointments by viewModel.appointments.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState(null)

    // Load appointments when component is first composed
    LaunchedEffect(patientId) {
        viewModel.getAppointmentsByPatient(patientId)
    }

    // Show error if any
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // Filter appointments by selected tab
    val filteredAppointments = remember(allAppointments, selectedTab) {
        val now = LocalDate.now()
        when (selectedTab) {
            "Past" -> allAppointments.filter {
                it.start_time.toLocalDate().isBefore(now)
            }
            "Current" -> allAppointments.filter {
                it.start_time.toLocalDate().isEqual(now)
            }
            "Upcoming" -> allAppointments.filter {
                it.start_time.toLocalDate().isAfter(now)
            }
            else -> emptyList()
        }
    }

    // Loading state
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .background(Color(0xFFF5F6F9), shape = RoundedCornerShape(5.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Past", "Current", "Upcoming").forEach { tab ->
                    val isSelected = tab == selectedTab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(if (isSelected) 5.dp else 0.dp)
                            .background(
                                if (isSelected) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { selectedTab = tab }
                            )
                    ) {
                        Text(
                            text = tab,
                            color = Color(0xFF232447),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (filteredAppointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No appointments found in this category",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAppointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onCancel = {
                                viewModel.deleteAppointment(appointment.id, patientId, true)
                            },
                            onReschedule = { 
                                // Navigate to reschedule screen
                                onReschedule(appointment)
                            },
                            onViewAppointment = {
                                // Navigate based on appointment status
                                when (appointment.status) {
                                    AppointmentStatus.COMPLETED -> onViewCompleted(appointment.id)
                                    else -> onViewConfirmed(appointment.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onCancel: () -> Unit,
    onReschedule: () -> Unit,
    onViewAppointment: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable(onClick = onViewAppointment),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dr. ${appointment.doctor_name}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${appointment.doctor_specialty}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4B5563)
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = onViewAppointment
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (appointment.status == AppointmentStatus.COMPLETED)
                                    PhosphorIcons.Bold.FilePdf
                                else
                                    PhosphorIcons.Bold.QrCode,
                                contentDescription = null,
                                modifier = Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(5.dp)),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (appointment.status == AppointmentStatus.COMPLETED)
                                    "Prescription"
                                else
                                    "Check In",
                                color = Color(0xFF4B5563),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                Image(
                    modifier = Modifier.weight(1f),
                    painter = painterResource(R.drawable.doctor),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Light.CalendarCheck,
                        contentDescription = null,
                    )
                    Text(
                        text = dateFormatter.format(appointment.start_time.toLocalDate()),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = PhosphorIcons.Light.Clock,
                        contentDescription = null,
                    )
                    val startTimeFormatted = timeFormatter.format(appointment.start_time)
                    val endTimeFormatted = timeFormatter.format(appointment.end_time)
                    Text(
                        text = "$startTimeFormatted - $endTimeFormatted",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (appointment.status == AppointmentStatus.CONFIRMED) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF3B82F6),
                        ),
                        border = BorderStroke(1.dp, Color(0xFF3B82F6)),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onReschedule,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Reschedule",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}