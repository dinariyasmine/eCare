package com.example.appointment.ui.screen.doctor

import com.example.appointment.ui.screen.components.list.HorizontalCalendar
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
import androidx.navigation.NavController
import com.example.appointment.navigation.Screen
import com.example.appointment.ui.screen.components.list.DayViewAgenda
import com.example.appointment.ui.screen.components.list.DoctorAppointmentsFilteredBar
import com.example.core.theme.ECareMobileTheme
import com.example.data.viewModel.AppointmentViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@Composable
fun ListAppointmentsDoctorScreen(
    viewModel: AppointmentViewModel,
    navController: NavController
) {
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
                selectedDate = selectedDate,
                viewModel = viewModel
            )

            DoctorAppointmentsFilteredBar(
                11,
                viewModel,
                onViewCompleted = { appointment ->
                navController.navigate(Screen.DoctorCompletedAppointment.createRoute(appointment.id.toString())
                ) },
                onViewConfirmed = { appointment ->
                    navController.navigate(Screen.DoctorConfirmedAppointment.createRoute(appointment.id.toString()))
                })
        }
    }

}