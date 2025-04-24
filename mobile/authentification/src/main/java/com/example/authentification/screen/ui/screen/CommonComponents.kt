package com.example.authentification.screen.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.CalendarDots
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.core.theme.Primary500
import com.example.core.theme.Gray500
import com.example.core.theme.Gray900
import com.example.core.theme.Gray100
import com.example.core.theme.Primary300
import com.example.core.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column {
        // Text field with calendar icon
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = modifier,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = PhosphorIcons.Bold.CalendarDots,
                        contentDescription = "Calendar",
                        tint = Primary500
                    )
                }
            }
        )

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Date(millis)
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                onValueChange(formatter.format(date))
                            }
                            showDatePicker = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary500
                        )
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDatePicker = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Primary500
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    title = { Text("Select Date") }
                )
            }
        }
    }
}


@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Gray500,
                textAlign = TextAlign.Start,
                fontSize = 14.sp
            )
        },
        modifier = modifier
            .height(50.dp),
        shape = RoundedCornerShape(5.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary500,
            unfocusedBorderColor = Primary300,
            cursorColor = Primary500,
            unfocusedContainerColor = Gray100,
            focusedContainerColor = Gray100
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        textStyle = TextStyle(
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
            color = Gray900
        ),
        singleLine = true,
        trailingIcon = trailingIcon
    )
}