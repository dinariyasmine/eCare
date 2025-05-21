@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appointment.ui.screen.doctor

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appointment.ui.screen.components.appoint.DatePicker
import com.example.appointment.ui.screen.components.availabilities.TimeSlotPickerDoctor
import com.example.core.theme.ECareMobileTheme
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListAvailabilitiesScreen(availabilityViewModel: AvailabilityViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val doctorId = 11
    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val selectedSlots = remember { mutableStateOf<Set<LocalDateTime>>(emptySet()) }

    val error by availabilityViewModel.error.collectAsState()
    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }

    ECareMobileTheme {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Manage Availability") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.border(
                                width = 1.dp,
                                color = Color(0xFF222B45),
                                shape = RectangleShape
                            )
                        )
                    }
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                DatePicker(
                    selectedDate = selectedDate.value,
                    onDateSelected = { newDate -> selectedDate.value = newDate }
                )

                Text("Available Time Slots", fontWeight = FontWeight.Medium)

                TimeSlotPickerDoctor(
                    selectedDate = selectedDate.value,
                    selectedSlots = selectedSlots.value,
                    doctorId = doctorId,
                    onSlotSelected = { slot ->
                        selectedSlots.value = if (selectedSlots.value.contains(slot)) {
                            selectedSlots.value - slot
                        } else {
                            selectedSlots.value + slot
                        }
                    },
                    availabilityViewModel = availabilityViewModel
                )
            }

            Button(
                onClick = {
                    if (selectedSlots.value.isEmpty()) {
                        Toast.makeText(context, "Please select at least one time slot", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val slotsByStartTime = selectedSlots.value.sortedBy { it }.groupConsecutiveDateTime()

                    // Delete all existing availabilities for this date and doctor
                    availabilityViewModel.availabilities.value
                        .filter { it.start_time.toLocalDate() == selectedDate.value && it.doctor_id == doctorId }
                        .forEach { availability ->
                            availabilityViewModel.deleteAvailability(availability.id)
                        }

                    // Create new availabilities
                    slotsByStartTime.forEach { (start, end) ->
                        val startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant())
                        val endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant())
                        availabilityViewModel.createAvailability(startDate, endDate)
                    }

                    Toast.makeText(context, "Availability updated successfully!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Save Availability",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun List<LocalDateTime>.groupConsecutiveDateTime(): List<Pair<LocalDateTime, LocalDateTime>> {
    if (isEmpty()) return emptyList()

    val sorted = this.sorted()
    val result = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()
    var currentStart = sorted.first()
    var currentEnd = sorted.first()

    for (i in 1 until sorted.size) {
        val currentTime = sorted[i]
        val prevTime = sorted[i - 1]

        if (prevTime.plusMinutes(30) == currentTime) {
            currentEnd = currentTime
        } else {
            result.add(currentStart to currentEnd.plusMinutes(30))
            currentStart = currentTime
            currentEnd = currentTime
        }
    }
    result.add(currentStart to currentEnd.plusMinutes(30))
    return result
}