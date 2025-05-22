package com.example.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ProvidedTypeConverter
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
    val qr_Code: String,
    val doctor_name: String?,
    val doctor_specialty: String?
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
    val QR_code: String,
    val doctor_name: String?,
    val doctor_specialty: String?,
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

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String = dateTime.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String): LocalDateTime =
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)

    @TypeConverter
    fun fromStatus(status: AppointmentStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): AppointmentStatus = AppointmentStatus.valueOf(value)

}

