package com.example.appointment.ui.screen.components.list

import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.QrCode
import com.adamglin.phosphoricons.light.CalendarCheck
import com.adamglin.phosphoricons.light.Clock
import android.os.Build
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adamglin.phosphoricons.Light
import com.example.appointment.R
import com.example.appointment.data.model.Appointment
import com.example.appointment.data.model.Status
import com.example.appointment.data.repository.AppointmentRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentsFilteredListBar() {
    val appointmentRepository = AppointmentRepository()
    val selectedTab = remember { mutableStateOf("Current") }
    val interactionSource = remember { MutableInteractionSource() }

    val allAppointments = remember {
        appointmentRepository.getAppointments().groupBy { appointment ->
            when {
                appointment.date.isBefore(LocalDate.now()) -> "Past"
                appointment.date.isAfter(LocalDate.now()) -> "Upcoming"
                else -> "Current"
            }
        }
    }

    val filteredAppointments = allAppointments[selectedTab.value] ?: emptyList()

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
                        .padding(
                             if (isSelected) 5.dp else 0.dp,
                        )
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
                AppointmentCard(appointment = appointment)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCard(appointment: Appointment) {
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
                    text = "Dr. ${appointment.doctor.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = appointment.doctor.specialty,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4B5563)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                when (appointment.status) {
                                    Status.Completed -> {

                                    }

                                    else -> {
                                    }
                                }
                            }
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding( vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Bold.QrCode,
                            contentDescription = null,
                            Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(5.dp)),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (appointment.status == Status.Completed)
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
                        text = "${appointment.startTime} - ${appointment.endTime}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (appointment.status == Status.Confirmed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { },
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
                        onClick = { },
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