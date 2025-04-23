import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onFormSubmit: (PatientFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    var formState by remember { mutableStateOf(PatientFormState()) }
    var ageExpanded by remember { mutableStateOf(false) }
    val ageRanges = listOf("18-25", "26-30", "31-40", "41-50", "51+")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Full Name Field
        Text(
            text = "Full name",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = formState.fullName,
            onValueChange = { formState = formState.copy(fullName = it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("John Doe") }
        )

        // Age Dropdown
        Text(
            text = "Age",
            style = MaterialTheme.typography.titleMedium
        )
        ExposedDropdownMenuBox(
            expanded = ageExpanded,
            onExpandedChange = { ageExpanded = !ageExpanded }
        ) {
            OutlinedTextField(
                value = formState.age,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ageExpanded) },
                placeholder = { Text("Select age range") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = ageExpanded,
                onDismissRequest = { ageExpanded = false }
            ) {
                ageRanges.forEach { range ->
                    DropdownMenuItem(
                        text = { Text(range) },
                        onClick = {
                            formState = formState.copy(age = range)
                            ageExpanded = false
                        }
                    )
                }
            }
        }

        // Gender Selection (Styled Buttons)
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
                onClick = { formState = formState.copy(gender = "Male") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (formState.gender == "Male") Color(0xFF3B82F6) else Color.White,
                    contentColor = if (formState.gender == "Male") Color.White else Color(0xFF6D7280)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE5E7EB)
                )
            ) {
                Text(
                    "Male",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Female Button
            Button(
                onClick = { formState = formState.copy(gender = "Female") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (formState.gender == "Female") Color(0xFF3B82F6) else Color.White,
                    contentColor = if (formState.gender == "Female") Color.White else Color(0xFF6D7280)
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE5E7EB)
                )
            ) {
                Text(
                    "Female",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Problem Description
        Text(
            text = "Describe your problem",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = formState.problemDescription,
            onValueChange = { formState = formState.copy(problemDescription = it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Write your problem") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            ),
            singleLine = false
        )


    }
}