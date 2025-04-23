package com.example.appointment.ui.screen.components.appoint

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Availability
import com.example.data.repository.AvailabilityRepository
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimeSlotPicker(
    selectedDate: Date,
    selectedSlot: String?,
    doctorId: Int,
    onSlotSelected: (String) -> Unit,
    availabilityRepository: AvailabilityRepository
) {
    val timeSlots = remember { mutableStateOf<List<TimeSlotUI>>(emptyList()) }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(selectedDate, doctorId) {
        isLoading.value = true
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = sdfDate.format(selectedDate)

        // Get doctor's availabilities for the selected date
        val availabilities = availabilityRepository.getAvailabilitiesByDoctorId(doctorId)
            .filter { sdfDate.format(it.start_time) == dateStr }

        // Convert to UI slots
        timeSlots.value = availabilities.flatMap { availability ->
            splitAvailabilityIntoSlots(availability)
        }

        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("Loading slots...")
        }
    } else if (timeSlots.value.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("No available slots")
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
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

private data class TimeSlotUI(
    val formattedTime: String,
    val isBooked: Boolean
)

private fun splitAvailabilityIntoSlots(availability: Availability): List<TimeSlotUI> {
    val slots = mutableListOf<TimeSlotUI>()
    val sdfTime = SimpleDateFormat("hh:mm a", Locale.getDefault())

    val calendar = Calendar.getInstance().apply { time = availability.start_time }
    val endTime = availability.end_time

    // Create 30-minute slots within the availability window
    while (calendar.time.before(endTime)) {
        val startTime = calendar.time
        calendar.add(Calendar.MINUTE, 30)
        if (calendar.time.after(endTime)) break

        slots.add(
            TimeSlotUI(
                formattedTime = sdfTime.format(startTime),
                isBooked = availability.booked
            )
        )
    }

    return slots
}

@Composable
private fun TimeSlotItem(
    slot: TimeSlotUI,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(100.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isSelected -> Color(0xFF4285F4) // Blue for selected
                    slot.isBooked -> Color.LightGray // Grey for booked
                    else -> Color.White // White for available
                }
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(
                enabled = !slot.isBooked,
                onClick = onClick
            )
    ) {
        Text(
            text = slot.formattedTime,
            color = when {
                isSelected -> Color.White
                slot.isBooked -> Color.DarkGray
                else -> Color.Black
            },
            fontSize = 14.sp
        )
    }
}