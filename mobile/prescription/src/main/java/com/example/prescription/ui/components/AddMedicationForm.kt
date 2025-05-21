package com.example.prescription.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.theme.Primary500
import com.example.data.model.Medication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationForm(
    medications: List<Medication>,
    onDismiss: () -> Unit,
    onAddMedication: (Medication, String, String, String, String) -> Unit
) {
    if (medications.isEmpty()) {
        onDismiss()
        return
    }

    var selectedMedicationIndex by remember { mutableStateOf(0) }
    var dosage by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf("once_daily") }
    var instructions by remember { mutableStateOf("") }

    var expandedMedication by remember { mutableStateOf(false) }
    var expandedFrequency by remember { mutableStateOf(false) }

    val frequencies = listOf(
        "once_daily" to "Once Daily",
        "twice_daily" to "Twice Daily",
        "three_daily" to "Three Times Daily",
        "four_daily" to "Four Times Daily",
        "as_needed" to "As Needed"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Medication",
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Medication Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedMedication,
                    onExpandedChange = { expandedMedication = it }
                ) {
                    OutlinedTextField(
                        value = medications[selectedMedicationIndex].name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Medication") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary500,
                            focusedLabelColor = Primary500
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedMedication,
                        onDismissRequest = { expandedMedication = false }
                    ) {
                        medications.forEachIndexed { index, medication ->
                            DropdownMenuItem(
                                text = { Text(medication.name) },
                                onClick = {
                                    selectedMedicationIndex = index
                                    expandedMedication = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dosage
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    placeholder = { Text("e.g., 1 capsule (250mg)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary500,
                        focusedLabelColor = Primary500
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Duration
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration") },
                    placeholder = { Text("e.g., 14 days") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary500,
                        focusedLabelColor = Primary500
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Frequency Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedFrequency,
                    onExpandedChange = { expandedFrequency = it }
                ) {
                    OutlinedTextField(
                        value = frequencies.find { it.first == selectedFrequency }?.second ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Frequency") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary500,
                            focusedLabelColor = Primary500
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedFrequency,
                        onDismissRequest = { expandedFrequency = false }
                    ) {
                        frequencies.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedFrequency = value
                                    expandedFrequency = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Instructions
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions (Optional)") },
                    placeholder = { Text("e.g., Take with food") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary500,
                        focusedLabelColor = Primary500
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (dosage.isNotBlank() && duration.isNotBlank()) {
                                onAddMedication(
                                    medications[selectedMedicationIndex],
                                    dosage,
                                    duration,
                                    selectedFrequency,
                                    instructions
                                )
                            }
                        },
                        enabled = dosage.isNotBlank() && duration.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary500,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}
