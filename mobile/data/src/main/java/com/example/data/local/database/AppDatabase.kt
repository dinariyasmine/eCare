package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.local.dao.PrescriptionDao
import com.example.data.local.entity.MedicationEntity
import com.example.data.local.entity.PrescriptionEntity
import com.example.data.local.entity.PrescriptionItemEntity
import com.example.data.local.entity.SyncStatusEntity
import java.util.Date

@Database(
    entities = [
        PrescriptionEntity::class,
        PrescriptionItemEntity::class,
        MedicationEntity::class,
        SyncStatusEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prescriptionDao(): PrescriptionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ecare_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}



class Converters {
    @TypeConverter // Utilisez @TypeConverter et non @TypeConverters
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter // Utilisez @TypeConverter et non @TypeConverters
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }


}

