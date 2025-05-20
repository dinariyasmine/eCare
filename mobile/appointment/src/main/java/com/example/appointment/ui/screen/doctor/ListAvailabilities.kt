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
import com.example.data.model.Availability
import com.example.data.viewModel.AvailabilityViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewAppointmentScreen(availabilityViewModel: AvailabilityViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Doctor ID would typically come from authentication or navigation arguments
    val doctorId = 11

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val selectedSlots = remember { mutableStateOf<Set<String>>(emptySet()) } // Track multiple selections
    val existingAvailabilities = remember { mutableStateOf<List<Availability>>(emptyList()) }

    // Fetch existing availabilities when date changes
    LaunchedEffect(selectedDate.value) {
        val date = Date.from(selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        existingAvailabilities.value = availabilityViewModel.availabilities.value
            .filter { sdf.format(it.start_time) == sdf.format(date) }
    }

    // Error handling
    val error by availabilityViewModel.error.collectAsState()
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            modifier = Modifier.border(width = 1.dp,
                                color = Color(0xFF222B45), shape = RectangleShape))
                    }
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                DatePicker(
                    selectedDate = selectedDate.value,
                    onDateSelected = { newDate -> selectedDate.value = newDate }
                )

                Text("Available Time Slots", fontWeight = FontWeight.Medium)
                val date = Date.from(selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant())

                TimeSlotPickerDoctor(
                    selectedDate = date,
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

                    // Convert selected slots to time ranges
                    val slotsByStartTime = selectedSlots.value.sorted().groupConsecutive()

                    // First delete all existing availabilities for this date
                    existingAvailabilities.value.forEach { availability ->
                        availabilityViewModel.deleteAvailability(availability.id)
                    }

                    // Then create new availabilities for the selected slots
                    slotsByStartTime.forEach { (start, end) ->
                        val startTime = parseTimeStringToDate(selectedDate.value, start)
                        val endTime = parseTimeStringToDate(selectedDate.value, end)
                        availabilityViewModel.createAvailability(startTime, endTime)
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

// Helper function to parse time string to Date
@RequiresApi(Build.VERSION_CODES.O)
private fun parseTimeStringToDate(date: LocalDate, timeString: String): Date {
    val timeParts = timeString.split(":")
    val hour = timeParts[0].toInt()
    val minute = timeParts[1].split(" ")[0].toInt()
    return Date.from(date.atTime(hour, minute).atZone(ZoneId.systemDefault()).toInstant())
}

// Helper function to group consecutive time slots
private fun List<String>.groupConsecutive(): List<Pair<String, String>> {
    if (isEmpty()) return emptyList()

    val sorted = this.sorted()
    val result = mutableListOf<Pair<String, String>>()
    var currentStart = sorted.first()
    var currentEnd = sorted.first()

    for (i in 1 until sorted.size) {
        val currentTime = sorted[i]
        val prevTime = sorted[i - 1]

        // Check if current time is 30 minutes after previous time
        if (areTimesConsecutive(prevTime, currentTime)) {
            currentEnd = currentTime
        } else {
            result.add(currentStart to currentEnd)
            currentStart = currentTime
            currentEnd = currentTime
        }
    }

    result.add(currentStart to currentEnd)
    return result
}

// Helper function to check if two time strings are consecutive (30 min apart)
private fun areTimesConsecutive(time1: String, time2: String): Boolean {
    val parts1 = time1.split(":")
    val parts2 = time2.split(":")

    val hour1 = parts1[0].toInt()
    val min1 = parts1[1].split(" ")[0].toInt()

    val hour2 = parts2[0].toInt()
    val min2 = parts2[1].split(" ")[0].toInt()

    return (hour1 == hour2 && min2 - min1 == 30) ||
            (hour2 - hour1 == 1 && min1 == 30 && min2 == 0)
}