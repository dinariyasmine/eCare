package com.example.appointment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.appointment.ui.screen.doctor.ListAppointmentsScreen
import com.example.appointment.ui.screen.doctor.ListAvailabilitiesScreen
import com.example.appointment.ui.screen.patient.NewAppointmentScreen
import com.example.appointment.ui.screen.patient.RescheduleAppointmentScreen
import com.example.appointment.ui.screen.patient.ViewConfirmedAppointmentScreen
import com.example.core.theme.ECareMobileTheme
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECareMobileTheme {
                val app = applicationContext as MyApplication
                val appointmentModel = AppointmentViewModel(app.appointmentRepository)
                val availabilityViewModel = AvailabilityViewModel(app.availabilityRepository, 11)
                //ListAppointmentsScreen(viewModel = appointmentModel)
                //NewAppointmentScreen(appointmentModel, availabilityViewModel = availabilityViewModel)
                //ViewConfirmedAppointmentScreen(appointmentModel, availabilityViewModel)
                //ListAppointmentsScreen(appointmentModel)
                ListAvailabilitiesScreen(availabilityViewModel)

            }
        }
    }
}
