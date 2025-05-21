package com.example.prescription.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.theme.Primary500
import com.example.data.model.Medication
import com.example.data.model.PrescriptionItem

@Composable
fun MedicationItem(item: PrescriptionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.medication.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Primary500
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dosage",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = item.dosage,
                        fontSize = 16.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Frequency",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = item.frequency.replace("_", " ").capitalize(),
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Duration",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = item.duration,
                        fontSize = 16.sp
                    )
                }
            }

            if (item.instructions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Instructions",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = item.instructions,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MedicationItemEditable(
    medication: Medication,
    dosage: String,
    duration: String,
    frequency: String,
    instructions: String,
    onRemove: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = medication.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Primary500,
                modifier = Modifier.weight(1f)
            )

            TextButton(onClick = onRemove) {
                Text("Remove", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dosage",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = dosage,
                    fontSize = 16.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Frequency",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = frequency.replace("_", " ").capitalize(),
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Duration",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = duration,
                    fontSize = 16.sp
                )
            }
        }

        if (instructions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Instructions",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Text(
                text = instructions,
                fontSize = 16.sp
            )
        }
    }
}
