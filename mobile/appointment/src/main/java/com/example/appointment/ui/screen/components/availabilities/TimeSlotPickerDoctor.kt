package com.example.appointment.ui.screen.components.availabilities

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Availability
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TimeSlotPickerDoctor(
    selectedDate: LocalDate,
    selectedSlots: Set<LocalDateTime>,
    doctorId: Int,
    onSlotSelected: (LocalDateTime) -> Unit,
    availabilityViewModel: AvailabilityViewModel
) {
    val context = LocalContext.current
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var slotToDelete by remember { mutableStateOf<LocalDateTime?>(null) }

    // Fetch availabilities when the component is first composed
    LaunchedEffect(selectedDate, doctorId) {
        availabilityViewModel.fetchAllAvailabilities(doctorId)
    }

    val allSlots = generateDailyTimeSlots(selectedDate)
    val backendAvailabilities = availabilityViewModel.availabilities.value.filter {
        it.start_time.toLocalDate() == selectedDate && it.doctor_id == doctorId
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation && slotToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteConfirmation = false
                slotToDelete = null
            },
            title = { Text("Delete Time Slot") },
            text = { Text("Are you sure you want to delete this time slot?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val existing = backendAvailabilities.find { it.start_time == slotToDelete }
                        existing?.let {
                            availabilityViewModel.deleteAvailability(it.id)
                            availabilityViewModel.fetchAllAvailabilities(doctorId)
                        }
                        showDeleteConfirmation = false
                        slotToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteConfirmation = false
                        slotToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        val rows = allSlots.chunked(3)
        rows.forEach { rowSlots ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowSlots.forEach { slot ->
                    val existing = backendAvailabilities.find { it.start_time == slot }
                    val backgroundColor = when {
                        existing?.booked == true -> Color(0xFF6D7280) // gray: booked
                        selectedSlots.contains(slot) || existing?.booked == false -> Color(0xFF3B82F6) // blue: selected or available in backend
                        else -> Color.White
                    }

                    val textColor = if (backgroundColor == Color.White) Color.Black else Color.White

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(1f)
                            .height(40.dp)
                            .background(backgroundColor, RoundedCornerShape(6.dp))
                            .clickable(enabled = existing?.booked != true) {
                                if (existing != null) {
                                    // Show confirmation dialog for deletion
                                    slotToDelete = slot
                                    showDeleteConfirmation = true
                                } else {
                                    // If slot is new, add it to backend
                                    val startDate = Date.from(slot.atZone(ZoneId.systemDefault()).toInstant())
                                    val endDate = Date.from(slot.plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant())
                                    
                                    // Create new availability without affecting existing ones
                                    availabilityViewModel.createAvailability(
                                        startTime = startDate,
                                        endTime = endDate
                                    )
                                    // Update local state
                                    onSlotSelected(slot)
                                    // Refresh availabilities after any change
                                    availabilityViewModel.fetchAllAvailabilities(doctorId)
                                }
                            }
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(6.dp)
                            )
                    ) {
                        Text(
                            text = formatTime(slot.toLocalTime()),
                            color = textColor,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun generateDailyTimeSlots(date: LocalDate): List<LocalDateTime> {
    val slots = mutableListOf<LocalDateTime>()
    var time = LocalTime.of(9, 0) // Start time
    val endTime = LocalTime.of(17, 30) // End time
    while (time <= endTime) {
        slots.add(LocalDateTime.of(date, time))
        time = time.plusMinutes(30)
    }
    return slots
}

private fun formatTime(time: LocalTime): String {
    val hour = time.hour % 12
    val displayHour = if (hour == 0) 12 else hour
    val minute = time.minute.toString().padStart(2, '0')
    val amPm = if (time.hour < 12) "AM" else "PM"
    return "$displayHour:$minute $amPm"
}
