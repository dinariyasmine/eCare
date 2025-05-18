package com.example.ecare_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appointment.ui.screen.patient.ViewConfirmedAppointmentScreen
import com.example.core.theme.ECareMobileTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECareMobileTheme {
                //ListAppointmentsScreen()
                //NewAppointmentScreen()
                ViewConfirmedAppointmentScreen()
            }
        }
    }
}
