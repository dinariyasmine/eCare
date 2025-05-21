package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Appointment
import androidx.room.TypeConverters
import com.example.data.model.Converters

// to be added all the classes of the app
@Database(entities = [Appointment::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getAppointmentDao(): AppointmentDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun createDataBase(context: Context): AppDatabase {
            var instance = INSTANCE
            if(instance == null) {
                instance = Room.databaseBuilder(context,
                    AppDatabase::class.java,"dbTeams")
                    .fallbackToDestructiveMigration() // to remove old data
                    .build()
                INSTANCE = instance
            }
            return instance
        }
    }
}