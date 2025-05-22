package com.example.prescription.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.theme.Primary500
import com.example.data.util.TokenManager
import com.example.data.viewModel.PrescriptionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionDetailScreen(
    prescriptionId: Int,
    navigateBack: () -> Unit,
    viewModel: PrescriptionViewModel = viewModel()
) {
    val context = LocalContext.current
    val prescription by viewModel.selectedPrescription.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isDoctor = TokenManager.getUserRole() == "doctor"

    LaunchedEffect(key1 = prescriptionId) {
        viewModel.fetchPrescriptionById(prescriptionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    prescription?.let { p ->
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(p.date)
                        val formattedDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date ?: Date())
                        Text("Prescription $formattedDate", fontWeight = FontWeight.Bold)
                    } ?: Text("Prescription Details", fontWeight = FontWeight.Bold)
                },
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
        },
        floatingActionButton = {
            prescription?.pdf_file?.let { pdfUrl ->
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(pdfUrl)
                        }
                        context.startActivity(intent)
                    },
                    containerColor = Primary500,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "View PDF")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary500
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${error ?: "Unknown error"}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchPrescriptionById(prescriptionId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary500
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
                prescription != null -> {
                    val p = prescription!!

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

                                // Get patient name safely from the complex structure
                                val patientName = when (val user = p.patient_details?.user) {
                                    is Map<*, *> -> user["name"] as? String ?: "Unknown Patient"
                                    else -> "Unknown Patient"
                                }

                                Text(
                                    text = patientName,
                                    fontSize = 16.sp
                                )

                                Text(
                                    text = "Patient ID: #${p.patient}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Doctor Information
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Doctor Information",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Get doctor name safely
                                val doctorName = when (val user = p.doctor_details?.user) {
                                    is Map<*, *> -> "Dr. ${user["name"] as? String ?: "Unknown"}"
                                    is Int -> "Dr. Unknown"
                                    else -> "Dr. Unknown"
                                }

                                Text(
                                    text = doctorName,
                                    fontSize = 16.sp
                                )

                                val specialty = p.doctor_details?.specialty ?: "General Practitioner"
                                Text(
                                    text = specialty,
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Prescription Date
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

                                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(p.date)
                                val formattedDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date ?: Date())

                                Text(
                                    text = "Date: $formattedDate",
                                    fontSize = 16.sp
                                )

                                if (!p.notes.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Notes: ${p.notes}",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Medications
                        Text(
                            text = "Medications",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (p.items.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "No medications in this prescription",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )
                            }

                            if (isDoctor) {
                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { /* Show add medication dialog */ },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Primary500
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Add a Medication")
                                }
                            }
                        } else {
                            // Display each medication
                            p.items.forEachIndexed { index, item ->
                                Text(
                                    text = "Medication ${index + 1}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        MedicationDetailRow(
                                            label = "Name:",
                                            value = item.medication.name
                                        )

                                        MedicationDetailRow(
                                            label = "Duration:",
                                            value = item.duration
                                        )

                                        MedicationDetailRow(
                                            label = "Dosage:",
                                            value = item.dosage
                                        )

                                        MedicationDetailRow(
                                            label = "Frequency:",
                                            value = item.frequency.replace("_", " ").capitalize()
                                        )


                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Instructions",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isDoctor) {
                                        // Editable view for doctors
                                        OutlinedTextField(
                                            value = item.instructions,
                                            onValueChange = { /* Update instructions */ },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            placeholder = { Text("Write the instructions for taking this medication.") }
                                        )
                                    } else {
                                        // Read-only view for patients
                                        Text(
                                            text = item.instructions.ifEmpty { "Take as directed by your doctor." },
                                            modifier = Modifier.padding(16.dp),
                                            fontSize = 16.sp
                                        )
                                    }
                                }

                                if (index < p.items.size - 1) {
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }

                            // Add Medication button for doctors
                            if (isDoctor) {
                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = { /* Show add medication dialog */ },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Primary500
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Add a Medication")
                                }
                            }
                        }

                        if (isDoctor) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    viewModel.generatePrescriptionPdf(prescriptionId) { pdfUrl ->
                                        pdfUrl?.let {
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                data = Uri.parse(it)
                                            }
                                            context.startActivity(intent)
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary500
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Generate Prescription")
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                    }
                }
            }
        }
    }
}

@Composable
fun MedicationDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.DarkGray
        )
    }
}
