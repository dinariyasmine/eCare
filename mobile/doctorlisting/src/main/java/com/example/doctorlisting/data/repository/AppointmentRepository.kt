package com.example.doctorlisting.data


import com.example.doctorlisting.data.model.Appointment




class AppointmentRepository {
    fun getAppointmentsForUser(userId: Int): List<Appointment> {
        return listOf(
            Appointment("09:00", "Dr. Rachid", "In Progress"),
            Appointment("11:00", "Dr. Jane", "Completed")
        )
    }
}
