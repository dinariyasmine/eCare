package com.example.appointment.ui.screen.components.appoint

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Availability
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun TimeSlotPicker(
    selectedDate: LocalDate,
    selectedSlot: String?,
    doctorId: Int,
    onSlotSelected: (String) -> Unit,
    availabilityViewModel: AvailabilityViewModel
) {
    val context = LocalContext.current
    val timeSlots = remember { mutableStateOf<List<TimeSlotUI>>(emptyList()) }
    val isLoading by availabilityViewModel.loading.collectAsState()
    val allAvailabilities by availabilityViewModel.availabilities.collectAsState()
    val error by availabilityViewModel.error.collectAsState()

    // Handle errors
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(selectedDate, allAvailabilities) {
        // Filter availabilities for the selected date
        val availabilities = allAvailabilities.filter { availability ->
            availability.start_time.toLocalDate() == selectedDate
        }

        // Convert to UI slots
        timeSlots.value = availabilities.flatMap { availability ->
            splitAvailabilityIntoSlots(availability)
        }.sortedBy { slot ->
            LocalTime.parse(slot.formattedTime, DateTimeFormatter.ofPattern("hh:mm a", Locale.US))
        }
    }

    Column {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Loading available slots...", style = MaterialTheme.typography.bodyMedium)
            }
        } else if (timeSlots.value.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No available slots for this date", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text(
                text = "Available Time Slots",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(timeSlots.value) { slot ->
                    TimeSlotItem(
                        slot = slot,
                        isSelected = slot.formattedTime == selectedSlot,
                        onClick = { onSlotSelected(slot.formattedTime) }
                    )
                }
            }
        }
    }
}

private data class TimeSlotUI(
    val formattedTime: String,
    val isBooked: Boolean,
    val availabilityId: Int
)

private fun splitAvailabilityIntoSlots(availability: Availability): List<TimeSlotUI> {
    val slots = mutableListOf<TimeSlotUI>()
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    var currentTime = availability.start_time
    val endTime = availability.end_time

    // Create 30-minute slots within the availability window
    while (currentTime.isBefore(endTime)) {
        val slotEndTime = currentTime.plusMinutes(30)
        if (slotEndTime.isAfter(endTime)) break

        slots.add(
            TimeSlotUI(
                formattedTime = currentTime.format(timeFormatter),
                isBooked = availability.booked,
                availabilityId = availability.id
            )
        )

        currentTime = slotEndTime
    }

    return slots
}

@Composable
private fun TimeSlotItem(
    slot: TimeSlotUI,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        slot.isBooked -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        slot.isBooked -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(100.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (slot.isBooked) Color.Transparent else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = !slot.isBooked,
                onClick = onClick
            )
    ) {
        Text(
            text = slot.formattedTime,
            color = textColor,
            fontSize = 14.sp,
            style = MaterialTheme.typography.labelMedium
        )
    }
}