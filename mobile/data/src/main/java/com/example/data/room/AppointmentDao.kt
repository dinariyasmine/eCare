package com.example.data.room

import androidx.room.Dao
import androidx.room.Query
import com.example.data.model.Appointment

@Dao
interface AppointmentDao {

    @Query("select * from appointment where patient_id = :id")
    suspend fun getAppointmentsByPatient(id: Int):List<Appointment>

    @Query("select * from appointment where doctor_id = :id")
    suspend fun getAppointmentsByDoctor(id: Int):List<Appointment>

}