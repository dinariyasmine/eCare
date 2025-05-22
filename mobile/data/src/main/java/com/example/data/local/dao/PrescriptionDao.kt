package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.PrescriptionEntity
import com.example.data.local.entity.PrescriptionItemEntity
import com.example.data.local.entity.MedicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrescriptionDao {
    @Query("SELECT * FROM prescriptions")
    fun getAllPrescriptionsFlow(): Flow<List<PrescriptionEntity>>

    @Query("SELECT * FROM prescriptions")
    suspend fun getAllPrescriptions(): List<PrescriptionEntity>

    @Query("SELECT * FROM prescriptions WHERE id = :id")
    suspend fun getPrescriptionById(id: Int): PrescriptionEntity?

    @Query("SELECT * FROM prescriptions WHERE localId = :localId")
    suspend fun getPrescriptionByLocalId(localId: String): PrescriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(prescription: PrescriptionEntity): Long

    @Update
    suspend fun updatePrescription(prescription: PrescriptionEntity)

    @Delete
    suspend fun deletePrescription(prescription: PrescriptionEntity)

    @Query("SELECT * FROM prescriptions WHERE isSynced = 0")
    suspend fun getUnsyncedPrescriptions(): List<PrescriptionEntity>

    @Query("UPDATE prescriptions SET isSynced = 1, id = :serverId WHERE localId = :localId")
    suspend fun markPrescriptionSynced(localId: String, serverId: Int)

    @Query("SELECT * FROM prescription_items WHERE prescriptionLocalId = :prescriptionLocalId")
    suspend fun getPrescriptionItems(prescriptionLocalId: String): List<PrescriptionItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptionItem(item: PrescriptionItemEntity): Long

    @Update
    suspend fun updatePrescriptionItem(item: PrescriptionItemEntity)

    @Query("SELECT * FROM prescription_items WHERE isSynced = 0")
    suspend fun getUnsyncedPrescriptionItems(): List<PrescriptionItemEntity>

    @Query("UPDATE prescription_items SET isSynced = 1, id = :serverId WHERE localId = :localId")
    suspend fun markPrescriptionItemSynced(localId: String, serverId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMedications(medications: List<MedicationEntity>)

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getMedicationById(id: Int): MedicationEntity?

    @Query("SELECT * FROM medications")
    suspend fun getAllMedications(): List<MedicationEntity>
}
