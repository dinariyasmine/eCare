package com.example.appointment.ui.screen.doctor

import com.example.appointment.ui.screen.components.list.HorizontalCalendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appointment.ui.screen.components.list.AppointmentsFilteredListBar
import com.example.appointment.ui.screen.components.list.DayViewAgenda
import com.example.appointment.ui.screen.components.list.DoctorAppointmentsFilteredBar
import com.example.core.theme.ECareMobileTheme
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListAppointmentsScreen() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    ECareMobileTheme {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            val currentDate = remember {
                val formatter = SimpleDateFormat(
                    "MMM d,yyyy",
                    Locale.ENGLISH
                )
                formatter.format(Date())
            }

            Text(
                text = currentDate,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Today",
                style = MaterialTheme.typography.displayMedium
            )

            HorizontalCalendar(
                onDateSelected = { date ->
                    selectedDate = date
                }
            )

            DayViewAgenda(
                selectedDate = selectedDate
            )

            DoctorAppointmentsFilteredBar(0)
        }
    }

}