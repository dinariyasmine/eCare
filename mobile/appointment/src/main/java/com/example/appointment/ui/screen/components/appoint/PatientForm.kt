package com.example.appointment.ui.screen.components.appoint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class PatientFormState(
    val fullName: String = "",
    val age: String = "",
    val gender: String = "",
    val problemDescription: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientForm(
    formState: PatientFormState,
    onValueChange: (PatientFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Full Name Field
        Text(
            text = "Full name",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = formState.fullName,
            onValueChange = { onValueChange(formState.copy(fullName = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("John Doe") },
            singleLine = true
        )

        // Age Field
        Text(
            text = "Age",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = formState.age,
            onValueChange = { onValueChange(formState.copy(age = it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        // Gender Selection
        Text(
            text = "Gender",
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Male Button
            Button(
                onClick = { onValueChange(formState.copy(gender = "MALE")) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (formState.gender == "MALE") Color(0xFF3B82F6) else Color.White,
                    contentColor = if (formState.gender == "MALE") Color.White else Color(0xFF6D7280)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE5E7EB)
                )
            ) {
                Text("Male")
            }

            // Female Button
            Button(
                onClick = { onValueChange(formState.copy(gender = "FEMALE")) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (formState.gender == "FEMALE") Color(0xFF3B82F6) else Color.White,
                    contentColor = if (formState.gender == "FEMALE") Color.White else Color(0xFF6D7280)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE5E7EB)
                )
            ) {
                Text("Female")
            }
        }

        // Problem Description
        Text(
            text = "Describe your problem",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = formState.problemDescription,
            onValueChange = { onValueChange(formState.copy(problemDescription = it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Describe your symptoms or concerns") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            ),
            singleLine = false
        )
    }
}