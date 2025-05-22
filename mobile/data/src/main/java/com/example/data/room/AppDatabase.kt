package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Appointment
import androidx.room.TypeConverters
import com.example.data.model.Converters

// to be added all the classes of the app
@Database(
    entities = [Appointment::class], // Add all your entities here
    version = 2, // Increment this number from previous version
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAppointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun createDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // This clears the database on version change
                    .addTypeConverter(Converters())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}