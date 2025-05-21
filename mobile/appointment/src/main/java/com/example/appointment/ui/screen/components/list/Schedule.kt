package com.example.appointment.ui.screen.components.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus
import com.example.data.viewModel.AppointmentViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayViewAgenda(
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
    viewModel: AppointmentViewModel
) {
    val appointments = remember(selectedDate) {
       viewModel.getAppointmentsForDate(selectedDate).sortedBy { it.start_time }
    }

    // For updating current time indicator
    var currentTime by remember { mutableStateOf(LocalTime.now()) }

    // Update current time every minute
    LaunchedEffect(key1 = true) {
        while (true) {
            delay(60000) // 60 seconds = 1 minute
            currentTime = LocalTime.now()
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header for the agenda
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedDate == LocalDate.now()) {
                    Text(
                        text = "Schedule Today",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    Text(
                        text = "Schedule",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Appointments list without time slots
            AppointmentsList(
                appointments = appointments,
                selectedDate = selectedDate,
                currentTime = currentTime
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppointmentsList(
    appointments: List<Appointment>,
    selectedDate: LocalDate,
    currentTime: LocalTime
) {
    val isToday = selectedDate == LocalDate.now()
    val pixelsPerMinute = 1.5.dp
    val timeFormatter = DateTimeFormatter.ofPattern("H:mm")
    val listState = rememberLazyListState()

    // Calculate scroll position to show current time
    LaunchedEffect(isToday, currentTime) {
        if (isToday) {
            // Calculate which hour item to scroll to
            val targetHour = currentTime.hour

            // Calculate the pixel offset within that hour
            val minuteOffset = (currentTime.minute * pixelsPerMinute.value).toInt()

            // Scroll to that hour with the minute offset
            listState.animateScrollToItem(
                index = targetHour,
                scrollOffset = minuteOffset
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(24) { hour ->
                val time = LocalTime.of(hour, 0)
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Time label
                    Box(
                        modifier = Modifier.width(35.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = time.format(timeFormatter),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF9CA3AF),
                        )
                    }

                    // Appointments column
                    Column(modifier = Modifier.weight(1f)) {
                        val hourAppointments = appointments.filter { it.start_time.hour == hour }
                            .sortedBy { it.start_time }

                        hourAppointments.forEach { appointment ->
                            val startOffset = appointment.start_time.minute * pixelsPerMinute.value
                            val duration = appointment.start_time.until(
                                appointment.end_time,
                                java.time.temporal.ChronoUnit.MINUTES
                            ) * pixelsPerMinute.value

                            if (appointment.start_time.minute > 0) {
                                Spacer(modifier = Modifier.height(startOffset.dp))
                            }

                            AppointmentCard(
                                appointment = appointment,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(duration.dp)
                                    .padding(end = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        if (hourAppointments.isEmpty()) {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            }
        }

        // Current time indicator - fixed glitching version
        if (isToday) {
            val currentMinutes = currentTime.hour * 60 + currentTime.minute
            val firstVisibleItem = remember { derivedStateOf { listState.firstVisibleItemIndex } }
            val scrollOffset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }

            // Calculate the absolute position of the current time
            val currentTimePosition = (currentMinutes * pixelsPerMinute.value).toInt()

            // Calculate the visible position relative to the scroll
            val visiblePosition = currentTimePosition - (firstVisibleItem.value * 60 * pixelsPerMinute.value).toInt() - scrollOffset.value

            // Only show if the current time is in the visible range
            if (visiblePosition >= 0 && visiblePosition <= remember { derivedStateOf { listState.layoutInfo.viewportEndOffset } }.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = visiblePosition.dp)
                        .zIndex(2f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(35.dp))

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFF3B82F6), shape = CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(Color(0xFF3B82F6))
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AppointmentCard(
    appointment: Appointment,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFF6FF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = "Dr. ${appointment.doctor_id}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )


            Box(
                modifier = Modifier
                    .background(
                        color = when (appointment.status) {
                            AppointmentStatus.CONFIRMED -> Color(0xFF9CA3AF)
                            AppointmentStatus.COMPLETED -> Color(0xFF3B82F6)
                            AppointmentStatus.IN_PROGRESS ->  Color(0xFF22C55E)
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = appointment.status.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White

                )
            }
        }
    }
}
