package com.example.appointment.ui.screen.components.availabilities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Availability
import com.example.data.viewModel.AvailabilityViewModel
import java.text.SimpleDateFormat
import java.util.*

private data class TimeSlotDoctorUI(
    val formattedTime: String,
    val isBooked: Boolean,
    val isAvailable: Boolean
)

@Composable
fun TimeSlotPickerDoctor(
    selectedDate: Date,
    selectedSlots: Set<String>,
    doctorId: Int,
    onSlotSelected: (String) -> Unit,
    availabilityViewModel: AvailabilityViewModel
) {
    val timeSlots = remember { mutableStateOf<List<TimeSlotDoctorUI>>(emptyList()) }
    val availabilities by availabilityViewModel.availabilities.collectAsState()
    val isLoading by availabilityViewModel.loading.collectAsState()

    LaunchedEffect(selectedDate, availabilities) {
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = sdfDate.format(selectedDate)

        // Get doctor's availabilities for the selected date
        val filteredAvailabilities = availabilities.filter {
            sdfDate.format(it.start_time) == dateStr
        }

        // Convert to UI slots
        timeSlots.value = generateAllTimeSlots().map { slot ->
            val isAvailable = filteredAvailabilities.any { availability ->
                isSlotInAvailability(slot, availability)
            }
            TimeSlotDoctorUI(
                formattedTime = slot,
                isAvailable = isAvailable,
                isBooked = false
            )
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(timeSlots.value) { slot ->
                TimeSlotItem(
                    slot = slot,
                    isSelected = selectedSlots.contains(slot.formattedTime),
                    onClick = { onSlotSelected(slot.formattedTime) }
                )
            }
        }
    }
}

// Generate all possible time slots for a day (30-minute intervals)
private fun generateAllTimeSlots(): List<String> {
    return (8..20).flatMap { hour ->
        listOf(
            String.format("%02d:00", hour),
            String.format("%02d:30", hour)
        )
    }
}

// Check if a time slot falls within an availability
private fun isSlotInAvailability(slot: String, availability: Availability): Boolean {
    val sdf = SimpleDateFormat("hh:mm", Locale.getDefault())
    val slotTime = sdf.parse(slot)
    val startTime = availability.start_time
    val endTime = availability.end_time

    return slotTime in startTime..endTime
}

private data class TimeSlotUI(
    val formattedTime: String,
    val isAvailable: Boolean,
    val isBooked: Boolean
)

@Composable
private fun TimeSlotItem(
    slot: TimeSlotDoctorUI,
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
                    slot.isAvailable -> Color(0xFFBB86FC) // Purple for existing availability
                    else -> Color.White // White for unselected
                }
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
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