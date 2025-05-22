package com.example.appointment

import android.app.Application
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.AppointmentSyncRepository
import com.example.data.repository.AvailabilityRepository
import com.example.data.retrofit.AppointmentEndpoint
import com.example.data.retrofit.AvailabilityEndpoint
import com.example.data.room.AppDatabase

class MyApplication: Application() {

    val appointmentDao by lazy { AppDatabase.createDatabase(this).getAppointmentDao() }
    val syncRepository by lazy { AppointmentSyncRepository(this) }
    val appointmentRepository by lazy {
        AppointmentRepository(
            appointmentEndpoint = AppointmentEndpoint.createInstance(),
            appointmentDao = appointmentDao,
            syncRepository = syncRepository
        )
    }

    val availabilityRepository by lazy { AvailabilityRepository(AvailabilityEndpoint.createInstance()) }
}