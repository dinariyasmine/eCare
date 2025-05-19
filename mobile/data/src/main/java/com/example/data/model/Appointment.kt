package com.example.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Appointment(
    @PrimaryKey val id: Int=0,
    val doctor_id: Int,
    val patient_id: Int,
    val date: LocalDate,
    val start_time: LocalTime,
    val end_time: LocalTime,
    val name: String,
    val gender: String,
    val age: String,
    val problem_description: String,
    val status: AppointmentStatus,
    val QR_code: String
)

enum class AppointmentStatus {
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED
}

data class AppointmentRequest(
    val doctor_id: Int,
    val patient_id: Int,
    val date: LocalDate,
    val start_time: LocalTime,
    val end_time: LocalTime,
    val name: String,
    val gender: String,
    val age: String,
    val problem_description: String
)

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(date: String): LocalDate = LocalDate.parse(date)

    @TypeConverter
    fun fromLocalTime(time: LocalTime): String = time.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalTime(time: String): LocalTime = LocalTime.parse(time)

    @TypeConverter
    fun fromStatus(status: AppointmentStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): AppointmentStatus = AppointmentStatus.valueOf(value)
}

