package com.example.prescription.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.theme.Primary500
import com.example.data.model.Medication
import com.example.data.viewModel.MedicationViewModel
import com.example.data.viewModel.PatientViewModel
import com.example.data.viewModel.PrescriptionViewModel
import com.example.prescription.ui.components.AddMedicationForm
import com.example.prescription.ui.components.MedicationItemEditable
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePrescriptionScreen(
    patientId: Int,
    navigateBack: () -> Unit,
    navigateToPrescriptionDetail: (Int) -> Unit,
    prescriptionViewModel: PrescriptionViewModel = viewModel(),
    patientViewModel: PatientViewModel = viewModel(),
    medicationViewModel: MedicationViewModel = viewModel()
) {
    val patient by patientViewModel.selectedPatient.collectAsState()
    val medications by medicationViewModel.medications.collectAsState(initial = emptyList())
    val isLoading by prescriptionViewModel.isLoading.collectAsState()

    var prescriptionDate by remember { mutableStateOf(Date()) }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddMedicationForm by remember { mutableStateOf(false) }

    // Store medication details directly in a list of tuples
    var medicationDetails by remember {
        mutableStateOf(listOf<MedicationDetail>())
    }

    LaunchedEffect(key1 = patientId) {
        patientViewModel.loadPatientById(patientId)
        medicationViewModel.fetchMedications()
    }

    val dateFormatter = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = prescriptionDate.time
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        prescriptionDate = Date(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Prescription", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary500,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary500
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Patient Information
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Patient Information",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = patient?.name ?: "Loading patient information...",
                                fontSize = 16.sp
                            )

                            Text(
                                text = "Patient ID: #$patientId",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Prescription Details
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Prescription Details",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Date Picker
                            OutlinedTextField(
                                value = dateFormatter.format(prescriptionDate),
                                onValueChange = { },
                                label = { Text("Date") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary500,
                                    focusedLabelColor = Primary500
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Notes
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Notes (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary500,
                                    focusedLabelColor = Primary500
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Medications
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Medications",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = { showAddMedicationForm = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Primary500
                                    )
                                ) {
                                    Text("Add Medication")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (medicationDetails.isEmpty()) {
                                Text(
                                    text = "No medications added yet",
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = Color.Gray
                                )
                            } else {
                                medicationDetails.forEachIndexed { index, detail ->
                                    MedicationItemEditable(
                                        medication = detail.medication,
                                        dosage = detail.dosage,
                                        duration = detail.duration,
                                        frequency = detail.frequency,
                                        instructions = detail.instructions,
                                        onRemove = {
                                            medicationDetails = medicationDetails.filterIndexed { i, _ -> i != index }
                                        }
                                    )

                                    if (index < medicationDetails.size - 1) {
                                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    // Create Button
                    Button(
                        onClick = {
                            // Add debug logs
                            Log.d("CreatePrescription", "Generate button clicked")
                            Log.d("CreatePrescription", "Medication details count: ${medicationDetails.size}")
                            val doctorId = 18
                            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(prescriptionDate)
                            Log.d("CreatePrescription", "Creating prescription for patient: $patientId, date: $dateString")

                            prescriptionViewModel.createPrescription(
                                patientId = patientId,
                                doctorId = doctorId,
                                date = dateString,
                                notes = notes,
                                onSuccess = { prescriptionId ->
                                    Log.d("CreatePrescription", "Prescription created with ID: $prescriptionId")

                                    // Add medications to the prescription
                                    medicationDetails.forEach { detail ->
                                        Log.d("CreatePrescription", "Adding medication: ${detail.medication.name}")
                                        prescriptionViewModel.addMedicationToPrescription(
                                            prescriptionId = prescriptionId,
                                            medicationId = detail.medication.medication_id,
                                            dosage = detail.dosage,
                                            duration = detail.duration,
                                            frequency = detail.frequency,
                                            instructions = detail.instructions
                                        )
                                    }

                                    // Generate PDF
                                    prescriptionViewModel.generatePrescriptionPdf(prescriptionId)

                                    // Navigate to prescription detail
                                    navigateToPrescriptionDetail(prescriptionId)
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = medicationDetails.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary500,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        Text("Generate Prescription")
                    }



                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showAddMedicationForm) {
        AddMedicationForm(
            medications = medications,
            onDismiss = { showAddMedicationForm = false },
            onAddMedication = { medication, dosage, duration, frequency, instructions ->
                medicationDetails = medicationDetails + MedicationDetail(
                    medication = medication,
                    dosage = dosage,
                    duration = duration,
                    frequency = frequency,
                    instructions = instructions
                )
                showAddMedicationForm = false
            }
        )
    }
}


data class MedicationDetail(
    val medication: Medication,
    val dosage: String,
    val duration: String,
    val frequency: String,
    val instructions: String
)
