package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Appointment

// to be added all the classes of the app
@Database(entities = [Appointment::class],version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getAppointmentDao(): AppointmentDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun createDataBase(context: Context): AppDatabase {
            var instance = INSTANCE
            if(instance == null) {
                instance = Room.databaseBuilder(context,
                    AppDatabase::class.java,"dbTeams").build()
                INSTANCE = instance }
            return instance
        }

    }

}