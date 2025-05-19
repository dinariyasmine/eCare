package com.example.appointment

import android.app.Application
import com.example.data.repository.AppointmentRepository
import com.example.data.repository.AvailabilityRepository
import com.example.data.retrofit.AppointmentEndpoint
import com.example.data.retrofit.AvailabilityEndpoint
import com.example.data.room.AppDatabase

class MyApplication: Application() {

    val appointmentDao by lazy { AppDatabase.createDataBase(this).getAppointmentDao() }
    val appointmentRepository by lazy { AppointmentRepository(AppointmentEndpoint.createInstance(), appointmentDao) }

    val availabilityRepository by lazy { AvailabilityRepository(AvailabilityEndpoint.createInstance()) }
}