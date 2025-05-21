package com.example.ecare_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appointment.MyApplication
import com.example.appointment.ui.screen.patient.ListAppointmentsScreen
import com.example.appointment.ui.screen.patient.ViewConfirmedAppointmentScreen
import com.example.core.theme.ECareMobileTheme
import com.example.data.model.Appointment
import com.example.data.viewModel.AppointmentViewModel
import com.example.data.viewModel.AvailabilityViewModel
import com.example.doctorlisting.ui.component.ScheduleCard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECareMobileTheme {

                val app = applicationContext as MyApplication
                val appointmentModel = AppointmentViewModel(app.appointmentRepository)
                val availabilityViewModel = AvailabilityViewModel(app.availabilityRepository, 11)
                ListAppointmentsScreen(
                    viewModel = appointmentModel,
                )
                //NewAppointmentScreen()
               // ViewConfirmedAppointmentScreen(appointmentModel, availabilityViewModel)
              //  ScheduleCard()
            }
        }
    }
}
