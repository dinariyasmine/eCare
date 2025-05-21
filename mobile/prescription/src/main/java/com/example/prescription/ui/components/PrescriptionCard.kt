package com.example.prescription.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Prescription
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PrescriptionCard(
    prescription: Prescription,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Prescription_${prescription.id}.pdf",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(prescription.date)
                val formattedDate = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(date ?: Date())

                Text(
                    text = formattedDate,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "View Details",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}
