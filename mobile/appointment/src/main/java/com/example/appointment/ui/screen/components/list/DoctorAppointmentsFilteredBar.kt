package com.example.appointment.ui.screen.components.list

import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.QrCode
import com.adamglin.phosphoricons.bold.FilePdf
import com.adamglin.phosphoricons.light.CalendarCheck
import com.adamglin.phosphoricons.light.Clock
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adamglin.phosphoricons.Light
import com.example.data.viewModel.AppointmentViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamglin.phosphoricons.bold.Article
import com.example.data.model.AppointmentStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorAppointmentsFilteredBar(doctorId: Int) {
    val viewModel: AppointmentViewModel = viewModel()
    val context = LocalContext.current
    val selectedTab = remember { mutableStateOf("Current") }
    val interactionSource = remember { MutableInteractionSource() }

    // State for appointments
    val allAppointments by viewModel.appointments.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Load appointments when component is first composed
    LaunchedEffect(doctorId) {
        isLoading = true
        try {
            viewModel.getAppointmentsByDoctor(doctorId)
        } catch (e: Exception) {
            error = "Failed to load appointments: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Show error if any
    if (error != null) {
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            error = null
        }
    }

    // Filter appointments by selected tab
    val filteredAppointments = remember(allAppointments, selectedTab.value) {
        allAppointments.groupBy { appointment ->
            when {
                appointment.date.isBefore(LocalDate.now()) -> "Past"
                appointment.date.isAfter(LocalDate.now()) -> "Upcoming"
                else -> "Current"
            }
        }[selectedTab.value] ?: emptyList()
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
        Column() {
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .background(Color(0xFFF5F6F9), shape = RoundedCornerShape(5.dp)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Past", "Current", "Upcoming").forEach { tab ->
                    val isSelected = tab == selectedTab.value
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
                                onClick = { selectedTab.value = tab }
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

            LazyColumn {
                items(filteredAppointments) { appointment ->
                    DoctorAppointmentCard(
                        appointment = appointment,
                        onCancel = {
                            viewModel.deleteAppointment(appointment.id, doctorId, false)
                        },
                        onReschedule = {
                            // Handle reschedule
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorAppointmentCard(
    appointment: com.example.data.model.Appointment,
    onCancel: () -> Unit,
    onReschedule: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault())
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)){
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(2f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        //${appointment.patient_name}
                        text = "Dr. ${appointment.patient_id}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Column (
                        modifier = Modifier.weight(2f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ){
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = {
                                        when (appointment.status) {
                                            AppointmentStatus.COMPLETED -> {
                                                // Handle prescription view
                                            }
                                            else -> {
                                                // Handle check-in
                                            }
                                        }
                                    }
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
                                    Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(5.dp)),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (appointment.status == AppointmentStatus.COMPLETED)
                                        "Prescription"
                                    else
                                        "Scan QR",
                                    color = Color(0xFF4B5563),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }

                        if (appointment.status != AppointmentStatus.COMPLETED) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null,
                                        onClick = {
                                            // Handle writing prescription
                                        }
                                    )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = PhosphorIcons.Bold.Article,
                                        contentDescription = null,
                                        Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(5.dp)),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Write Prescription",
                                        color = Color(0xFF4B5563),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        }
                    }


                }

            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Light.CalendarCheck,
                        contentDescription = null,
                    )
                    Text(formatter.format(appointment.date))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = PhosphorIcons.Light.Clock,
                        contentDescription = null,
                    )
                    Text(
                        text = "${appointment.start_time} - ${appointment.end_time}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (appointment.status == AppointmentStatus.CONFIRMED) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                        border = BorderStroke(1.dp, Color(0xFF3B82F6),),
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