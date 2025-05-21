package com.example.doctorlisting.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import com.adamglin.PhosphorIcons
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Appointment
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleCard(appointments: List<Appointment>) {
    val today = LocalDate.now()
    val zoneId = ZoneId.of("Africa/Algiers")
    val now = ZonedDateTime.now(zoneId).withSecond(0).withNano(0)

    val todayAppointments = appointments
        .filter {
            it.start_time.hour == today.dayOfMonth
        }

        .sortedBy { it.start_time}

    val appointmentHours = todayAppointments.map { it.start_time}.distinct()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Schedule Today", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val hoursToDisplay = (appointmentHours + now.hour).distinct()

        for (hour in hoursToDisplay) {
            val hourFormatted = "%02d:00".format(hour)

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                // Hour label on the left
                Column(modifier = Modifier.width(60.dp)) {
                    Text(text = hourFormatted, color = Color.Gray, fontSize = 12.sp)
                }

                // Timeline and appointment area
                Column(modifier = Modifier.weight(1f)) {
                    // Draw current time indicator if it's this hour
                    if (hour == now.hour) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Canvas(modifier = Modifier.size(10.dp)) {
                                drawCircle(color = Color(0xFF4A90E2))
                            }
                            Divider(
                                color = Color(0xFF4A90E2),
                                thickness = 2.dp,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    todayAppointments
                        .filter { it.start_time == hour }
                        .forEach { appointment ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(appointment.doctor_id.toString(), style = MaterialTheme.typography.body1)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF4A90E2), shape = RoundedCornerShape(8.dp))
                                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text(appointment.patient_id.toString(), color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }


        if (todayAppointments.isEmpty()) {
            Text("No appointments today.", color = Color.Gray)
        }
    }
}
