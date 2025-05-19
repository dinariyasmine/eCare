@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.appointment.ui.screen.patient

import PatientForm
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.QrCode
import com.example.appointment.ui.screen.components.appoint.DatePicker
import com.example.appointment.ui.screen.components.appoint.TimeSlotPicker
import com.example.core.theme.ECareMobileTheme
import com.example.data.repository.AvailabilityRepository
import com.example.data.retrofit.AvailabilityEndpoint
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewConfirmedAppointmentScreen(viewModel: AppointmentViewModel, availabilityViewModel: AvailabilityViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedDate = remember { mutableStateOf(LocalDate.now()) }
    val selectedSlot = remember { mutableStateOf<String?>(null) }

    // Error handling
    val error by viewModel.error.observeAsState()
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
                    title = { Text("Your appointment is confirmed!") },
                    navigationIcon = {
                        IconButton(onClick = { /* Keep back arrow clickable */ }) {
                            Icon(
                                Icons.Default.ArrowBack, contentDescription = "Back",
                                modifier = Modifier.border(
                                    width = 1.dp,
                                    color = Color(0xFF222B45), shape = RectangleShape
                                )
                            )
                        }
                    }
                )

                // Disabled overlay for all content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { /* Disabled */ }
                ) {
                    Column(modifier = Modifier.alpha(0.6f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = PhosphorIcons.Bold.QrCode,
                                contentDescription = null,
                                Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(5.dp)),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Check In with QR Code",
                                color = Color(0xFF4B5563),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                Column(modifier = Modifier.padding(16.dp)) {

                            // Disabled DatePicker
                            Box(modifier = Modifier.clickable(enabled = false) {}) {
                                DatePicker(
                                    selectedDate = selectedDate.value,
                                    onDateSelected = { /* Disabled */ }
                                )
                            }

                            Text("Available Time", fontWeight = FontWeight.Medium)
                            val date = Date.from(
                                selectedDate.value.atStartOfDay(ZoneId.systemDefault()).toInstant()
                            )

                            // Disabled TimeSlotPicker
                            Box(modifier = Modifier.clickable(enabled = false) {}) {
                                TimeSlotPicker(
                                    selectedDate = date,
                                    selectedSlot = selectedSlot.value,
                                    doctorId = 101,
                                    onSlotSelected = { /* Disabled */ },
                                    availabilityViewModel = availabilityViewModel
                                )
                            }
                        }

                        Text("Patient Details", fontWeight = FontWeight.Medium)
                        // Disabled PatientForm
                        Box(modifier = Modifier.clickable(enabled = false) {}) {
                            PatientForm(
                                onFormSubmit = { /* Disabled */ }
                            )
                        }

                        // Disabled Button
                        Button(
                            onClick = { /* Disabled */ },
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF60A5FA), // Gray color
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF60A5FA),
                                disabledContentColor = Color.White
                            )
                        ) {
                            Text(
                                "Set the Appointment",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
    }
}