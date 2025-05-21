package com.example.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class AppointmentDto(
    val id: Int,
    val doctor: Int,
    val patient: Int,
    val start_time: String,
    val end_time: String,
    val name: String,
    val gender: String,
    val age: String,
    val problem_description: String,
    val status: String,
    val qr_Code: String
)

@Entity
data class Appointment(
    @PrimaryKey val id: Int=0,
    val doctor_id: Int,
    val patient_id: Int,
    val start_time: LocalDateTime,
    val end_time: LocalDateTime,
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
    val doctor: Int,
    val patient: Int,
    val start_time: String,
    val end_time: String,
    val name: String,
    val gender: String,
    val age: String,
    val problem_description: String
)

class Converters {

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String = dateTime.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String): LocalDateTime =
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)

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

