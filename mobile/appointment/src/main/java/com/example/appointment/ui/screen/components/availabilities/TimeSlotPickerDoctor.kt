package com.example.appointment.ui.screen.components.availabilities

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TimeSlotPickerDoctor(
    selectedDate: LocalDate,
    selectedSlots: Set<LocalDateTime>,
    doctorId: Int,
    onSlotSelected: (LocalDateTime) -> Unit,
    availabilityViewModel: AvailabilityViewModel
) {
    val allSlots = generateDailyTimeSlots(selectedDate)
    val backendAvailabilities = availabilityViewModel.availabilities.value.filter {
        it.start_time.toLocalDate() == selectedDate && it.doctor_id == doctorId
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
                                onSlotSelected(slot)
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
