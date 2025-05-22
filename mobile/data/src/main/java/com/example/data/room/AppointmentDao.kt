package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.example.data.model.Appointment
import com.example.data.model.AppointmentStatus

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)

    @Query("SELECT * FROM appointment WHERE patient_id = :id")
    suspend fun getAppointmentsByPatient(id: Int): List<Appointment>

    @Query("SELECT * FROM appointment WHERE doctor_id = :id")
    suspend fun getAppointmentsByDoctor(id: Int): List<Appointment>

    @Query("SELECT * FROM appointment WHERE id = :id")
    suspend fun getAppointmentById(id: Int): Appointment?

    @Query("SELECT * FROM appointment WHERE status = :status")
    suspend fun getAppointmentsByStatus(status: String): List<Appointment>

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)
}