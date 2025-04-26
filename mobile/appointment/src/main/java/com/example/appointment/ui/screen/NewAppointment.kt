@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appointment.ui.screen

import PatientForm
import PatientFormState
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appointment.ui.screen.components.appoint.DatePicker
import com.example.appointment.ui.screen.components.appoint.TimeSlotPicker
import com.example.appointment.ui.theme.ECareMobileTheme
import com.example.data.repository.AvailabilityRepository
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewAppointmentScreen(){
    ECareMobileTheme {
        Column (
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("New Appointment") },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.border(width = 1.dp, color = Color(0xFF222B45), shape = RectangleShape))
                    }
                }
            )
            val selectedDate = remember { mutableStateOf(LocalDate.now()) }
            val selectedSlot = remember { mutableStateOf<String?>(null) }
            val availabilityRepository = remember { AvailabilityRepository() }

            Column(modifier = Modifier.padding(16.dp)) {
                DatePicker(
                    selectedDate = selectedDate.value,
                    onDateSelected = { newDate -> selectedDate.value = newDate }
                )



                Text("Available Time", fontWeight = FontWeight.Medium)
                val date = Date.from(selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant())

                TimeSlotPicker(
                    selectedDate = date,
                    selectedSlot = selectedSlot.value,
                    doctorId = 101,
                    onSlotSelected = { slot -> selectedSlot.value = slot },
                    availabilityRepository = availabilityRepository
                )
            }
        Text("Patient Details", fontWeight = FontWeight.Medium)
            var formData = remember { mutableStateOf<PatientFormState?>(null) }
            PatientForm(
                onFormSubmit = { submittedData ->
                    formData.value = submittedData
                    // You can now use the form data as needed
                    println("Form submitted: $submittedData")
                }
            )

            // Example of using the collected data
            formData.value?.let { data ->
                Text("Collected data:")
                Text("Name: ${data.fullName}")
                Text("Age: ${data.age}")
                Text("Gender: ${data.gender}")
                Text("Problem: ${data.problemDescription}")
            }
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
                    .padding(5.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Set the  Appointment ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }

}