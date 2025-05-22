package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.example.data.local.database.AppDatabase
import com.example.data.local.entity.MedicationEntity
import com.example.data.local.entity.PrescriptionEntity
import com.example.data.local.entity.PrescriptionItemEntity
import com.example.data.model.Medication
import com.example.data.model.Prescription
import com.example.data.model.PrescriptionItem
import com.example.data.network.ApiService
import com.example.data.sync.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class PrescriptionRepository(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    private val context: Context
) {
    private val prescriptionDao = appDatabase.prescriptionDao()

    // Get prescriptions as a Flow for reactive UI updates
    fun getPrescriptionsFlow(): Flow<List<Prescription>> {
        return prescriptionDao.getAllPrescriptionsFlow().map { entities ->
            entities.map { it.toPrescription() }
        }
    }

    suspend fun getPrescriptions(): List<Prescription> = withContext(Dispatchers.IO) {
        try {
            if (NetworkUtils.isNetworkAvailable(context)) {
                // Online: Get from API and update local database
                Log.d("PrescriptionRepository", "Fetching prescriptions from API")
                val prescriptions = apiService.getPrescriptions()

                // Store in local database
                prescriptions.forEach { prescription ->
                    try {
                        savePrescriptionLocally(prescription)
                    } catch (e: Exception) {
                        Log.e("PrescriptionRepository", "Error saving prescription locally: ${e.message}", e)
                    }
                }

                prescriptions
            } else {
                // Offline: Get from local database
                Log.d("PrescriptionRepository", "Fetching prescriptions from local database")
                prescriptionDao.getAllPrescriptions().map { it.toPrescription() }
            }
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error fetching prescriptions: ${e.message}", e)
            // Fallback to local data if API call fails
            prescriptionDao.getAllPrescriptions().map { it.toPrescription() }
        }
    }

    suspend fun getPrescriptionById(id: Int): Prescription = withContext(Dispatchers.IO) {
        try {
            if (NetworkUtils.isNetworkAvailable(context)) {
                // Online: Get from API and update local database
                Log.d("PrescriptionRepository", "Fetching prescription with ID: $id from API")
                try {
                    val prescription = apiService.getPrescriptionById(id)
                    try {
                        savePrescriptionLocally(prescription)
                    } catch (e: Exception) {
                        Log.e("PrescriptionRepository", "Error saving prescription locally: ${e.message}", e)
                    }
                    prescription
                } catch (e: Exception) {
                    Log.e("PrescriptionRepository", "Error fetching from API: ${e.message}", e)
                    // Si l'API échoue, essayer de récupérer localement
                    val localPrescription = findPrescriptionLocally(id)
                    localPrescription ?: throw Exception("Prescription not found")
                }
            } else {
                // Offline: Get from local database
                Log.d("PrescriptionRepository", "Fetching prescription with ID: $id from local database")
                val localPrescription = findPrescriptionLocally(id)
                localPrescription ?: throw Exception("Prescription not found locally")
            }
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error fetching prescription: ${e.message}", e)
            throw Exception("Prescription not found")
        }
    }

    // Méthode d'aide pour trouver une prescription localement, qui gère les IDs négatifs
    private suspend fun findPrescriptionLocally(id: Int): Prescription? {
        try {
            // Cas 1: ID positif - prescription synchronisée avec le serveur
            if (id > 0) {
                val entity = prescriptionDao.getPrescriptionById(id)
                if (entity != null) {
                    return entity.toPrescription()
                }
            }

            // Cas 2: ID négatif - prescription créée localement
            if (id < 0 || id == -1) {
                Log.d("PrescriptionRepository", "Looking for locally created prescription with negative ID")
                // Récupérer toutes les prescriptions non synchronisées
                val unsyncedPrescriptions = prescriptionDao.getUnsyncedPrescriptions()

                // Trouver la première prescription non synchronisée (si plusieurs existent)
                if (unsyncedPrescriptions.isNotEmpty()) {
                    Log.d("PrescriptionRepository", "Found ${unsyncedPrescriptions.size} unsynced prescriptions")
                    return unsyncedPrescriptions.first().toPrescription()
                }
            }

            // Cas 3: Recherche par localId si c'est une chaîne de caractères (UUID)
            if (id.toString().length > 8) { // Heuristique simple pour détecter un UUID
                val entity = prescriptionDao.getPrescriptionByLocalId(id.toString())
                if (entity != null) {
                    return entity.toPrescription()
                }
            }

            return null
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error finding prescription locally: ${e.message}", e)
            return null
        }
    }

    suspend fun createPrescription(
        patientId: Int,
        doctorId: Int,
        date: String,
        notes: String
    ): Int = withContext(Dispatchers.IO) {
        try {
            if (NetworkUtils.isNetworkAvailable(context)) {
                // Online: Create on server
                Log.d("PrescriptionRepository", "Creating prescription on server")
                val data: Map<String, Any> = mapOf(
                    "patient" to patientId,
                    "doctor" to doctorId,
                    "date" to date,
                    "notes" to notes
                )

                val prescription = apiService.createPrescription(data)

                // Save to local database
                try {
                    savePrescriptionLocally(prescription)
                } catch (e: Exception) {
                    Log.e("PrescriptionRepository", "Error saving prescription locally: ${e.message}", e)
                }

                prescription.id
            } else {
                // Offline: Save locally with pending CREATE operation
                Log.d("PrescriptionRepository", "Creating prescription locally (offline)")
                val localId = UUID.randomUUID().toString()
                val prescriptionEntity = PrescriptionEntity(
                    id = null, // Will be set after sync
                    localId = localId,
                    patient = patientId,
                    doctor = doctorId,
                    date = date,
                    notes = notes,
                    pdfFile = null,
                    createdAt = null,
                    updatedAt = null,
                    isSynced = false,
                    pendingOperation = "CREATE"
                )

                prescriptionDao.insertPrescription(prescriptionEntity)

                // Return a temporary negative ID to indicate it's not synced yet
                -1
            }
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error creating prescription: ${e.message}", e)

            // Save locally if API call fails
            val localId = UUID.randomUUID().toString()
            val prescriptionEntity = PrescriptionEntity(
                id = null,
                localId = localId,
                patient = patientId,
                doctor = doctorId,
                date = date,
                notes = notes,
                pdfFile = null,
                createdAt = null,
                updatedAt = null,
                isSynced = false,
                pendingOperation = "CREATE"
            )

            prescriptionDao.insertPrescription(prescriptionEntity)

            // Return a temporary negative ID
            -1
        }
    }

    suspend fun addMedicationToPrescription(
        prescriptionId: Int,
        medicationId: Int,
        dosage: String,
        duration: String,
        frequency: String,
        instructions: String
    ) = withContext(Dispatchers.IO) {
        try {
            if (NetworkUtils.isNetworkAvailable(context)) {
                // Online: Add to server
                Log.d("PrescriptionRepository", "Adding medication to prescription on server")

                // Récupérer la prescription pour obtenir le doctorId
                val prescription = try {
                    findPrescriptionLocally(prescriptionId) ?: apiService.getPrescriptionById(prescriptionId)
                } catch (e: Exception) {
                    Log.e("PrescriptionRepository", "Error fetching prescription: ${e.message}", e)
                    throw Exception("Cannot get prescription details")
                }

                val doctorId =18
                val data: Map<String, Any> = mapOf(
                    "medication_id" to medicationId,
                    "dosage" to dosage,
                    "duration" to duration,
                    "frequency" to frequency,
                    "instructions" to instructions,
                    "prescribed_by" to doctorId // Utiliser le doctorId de la prescription
                )

                Log.d("PrescriptionRepository", "Request data: ${data}")

                val response = apiService.addMedicationToPrescription(prescriptionId, data)

                // Update local database
                val updatedPrescription = apiService.getPrescriptionById(prescriptionId)
                try {
                    savePrescriptionLocally(updatedPrescription)
                } catch (e: Exception) {
                    Log.e("PrescriptionRepository", "Error saving updated prescription locally: ${e.message}", e)
                }
            } else {
                // Offline: Save locally
                Log.d("PrescriptionRepository", "Adding medication to prescription locally (offline)")

                // First, find the prescription entity by ID or create a placeholder
                val prescriptionEntity = if (prescriptionId < 0) {
                    // Pour les prescriptions créées localement avec ID négatif
                    prescriptionDao.getUnsyncedPrescriptions().firstOrNull()
                } else {
                    prescriptionDao.getPrescriptionById(prescriptionId)
                }

                if (prescriptionEntity == null) {
                    throw Exception("Prescription not found locally")
                }

                // Vérifier si le médicament existe déjà
                val medicationExists = prescriptionDao.getMedicationById(medicationId) != null

                // Si le médicament n'existe pas, créer une entité de base
                if (!medicationExists) {
                    val medicationEntity = MedicationEntity(
                        id = medicationId,
                        name = "Medication $medicationId", // Nom temporaire
                        description = null,
                        created_at = null,
                        updated_at = null
                    )
                    prescriptionDao.insertMedication(medicationEntity)
                }

                // Create prescription item entity
                val prescriptionItemEntity = PrescriptionItemEntity(
                    id = null,
                    prescriptionLocalId = prescriptionEntity.localId,
                    medicationId = medicationId,
                    dosage = dosage,
                    duration = duration,
                    frequency = frequency,
                    instructions = instructions,
                    isSynced = false
                )

                prescriptionDao.insertPrescriptionItem(prescriptionItemEntity)
            }
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error adding medication: ${e.message}", e)

            // Try to save locally if API call fails
            try {
                val prescriptionEntity = if (prescriptionId < 0) {
                    // Pour les prescriptions créées localement avec ID négatif
                    prescriptionDao.getUnsyncedPrescriptions().firstOrNull()
                } else {
                    prescriptionDao.getPrescriptionById(prescriptionId)
                }

                if (prescriptionEntity != null) {
                    // Vérifier si le médicament existe déjà
                    val medicationExists = prescriptionDao.getMedicationById(medicationId) != null

                    // Si le médicament n'existe pas, créer une entité de base
                    if (!medicationExists) {
                        val medicationEntity = MedicationEntity(
                            id = medicationId,
                            name = "Medication $medicationId", // Nom temporaire
                            description = null,
                            created_at = null,
                            updated_at = null
                        )
                        prescriptionDao.insertMedication(medicationEntity)
                    }

                    val prescriptionItemEntity = PrescriptionItemEntity(
                        id = null,
                        prescriptionLocalId = prescriptionEntity.localId,
                        medicationId = medicationId,
                        dosage = dosage,
                        duration = duration,
                        frequency = frequency,
                        instructions = instructions,
                        isSynced = false
                    )

                    prescriptionDao.insertPrescriptionItem(prescriptionItemEntity)
                } else {
                    throw e
                }
            } catch (innerException: Exception) {
                Log.e("PrescriptionRepository", "Error during local fallback: ${innerException.message}", innerException)
                throw e
            }
        }
    }

    suspend fun generatePrescriptionPdf(prescriptionId: Int): String? = withContext(Dispatchers.IO) {
        try {
            Log.d("PrescriptionRepository", "Generating PDF for prescription $prescriptionId")
            val responseBody = apiService.generatePrescriptionPdf(prescriptionId)

            // Save the PDF to a file or return the URL from another endpoint
            val pdfUrl = "http://ea18-105-102-48-10.ngrok-free.app/prescriptions/prescription_$prescriptionId.pdf"
            Log.d("PrescriptionRepository", "PDF generated: $pdfUrl")
            pdfUrl
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error generating PDF: ${e.message}", e)
            null
        }
    }

    suspend fun syncPrescriptions(): Boolean = withContext(Dispatchers.IO) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return@withContext false
        }

        try {
            Log.d("PrescriptionRepository", "Starting prescription sync")

            // 1. Sync unsynced prescriptions
            val unsyncedPrescriptions = prescriptionDao.getUnsyncedPrescriptions()
            Log.d("PrescriptionRepository", "Found ${unsyncedPrescriptions.size} unsynced prescriptions")

            for (prescriptionEntity in unsyncedPrescriptions) {
                when (prescriptionEntity.pendingOperation) {
                    "CREATE" -> {
                        // Create prescription on server
                        val data: Map<String, Any> = mapOf(
                            "patient" to prescriptionEntity.patient,
                            "doctor" to prescriptionEntity.doctor,
                            "date" to prescriptionEntity.date,
                            "notes" to (prescriptionEntity.notes ?: "")
                        )

                        val createdPrescription = apiService.createPrescription(data)

                        // Update local entity with server ID and mark as synced
                        prescriptionDao.markPrescriptionSynced(
                            prescriptionEntity.localId,
                            createdPrescription.id
                        )

                        // Sync prescription items
                        val prescriptionItems = prescriptionDao.getPrescriptionItems(prescriptionEntity.localId)
                        for (item in prescriptionItems) {

                            val itemData: Map<String, Any> = mapOf(
                                "medication_id" to item.medicationId,
                                "dosage" to item.dosage,
                                "duration" to item.duration,
                                "frequency" to item.frequency,
                                "instructions" to item.instructions,
                                )

                            apiService.addMedicationToPrescription(createdPrescription.id, itemData)
                        }
                    }
                    // Handle UPDATE and DELETE operations if needed
                }
            }

            // 2. Fetch latest data from server to ensure consistency
            val serverPrescriptions = apiService.getPrescriptions()
            for (prescription in serverPrescriptions) {
                try {
                    savePrescriptionLocally(prescription)
                } catch (e: Exception) {
                    Log.e("PrescriptionRepository", "Error saving prescription locally during sync: ${e.message}", e)
                }
            }

            Log.d("PrescriptionRepository", "Prescription sync completed successfully")
            true
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error during prescription sync: ${e.message}", e)
            false
        }
    }

    private suspend fun savePrescriptionLocally(prescription: Prescription) {
        try {
            // Utiliser une transaction pour garantir l'intégrité des données
            appDatabase.withTransaction {
                // 1. D'abord, insérer la prescription
                val prescriptionEntity = PrescriptionEntity(
                    id = prescription.id,
                    localId = prescription.id.toString(), // Use server ID as local ID for synced prescriptions
                    patient = prescription.patient,
                    doctor = prescription.doctor,
                    date = prescription.date,
                    notes = prescription.notes,
                    pdfFile = prescription.pdf_file,
                    createdAt = prescription.created_at,
                    updatedAt = prescription.updated_at,
                    isSynced = true,
                    pendingOperation = null
                )

                prescriptionDao.insertPrescription(prescriptionEntity)

                // 2. Ensuite, pour chaque item, insérer d'abord le médicament, puis l'item
                prescription.items.forEach { item ->
                    // Insérer le médicament avant l'item
                    val medicationEntity = MedicationEntity(
                        id = item.medication.id,
                        name = item.medication.name,
                        description = item.medication.description,
                        created_at = item.medication.created_at,
                        updated_at = item.medication.updated_at
                    )

                    prescriptionDao.insertMedication(medicationEntity)

                    // Maintenant, insérer l'item de prescription
                    val itemEntity = PrescriptionItemEntity(
                        id = item.id,
                        prescriptionLocalId = prescription.id.toString(),
                        medicationId = item.medication.id,
                        dosage = item.dosage,
                        duration = item.duration,
                        frequency = item.frequency,
                        instructions = item.instructions,

                        isSynced = true
                    )

                    prescriptionDao.insertPrescriptionItem(itemEntity)
                }
            }
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error saving prescription locally: ${e.message}", e)
            throw e
        }
    }

    // Extension function to convert PrescriptionEntity to Prescription
    private suspend fun PrescriptionEntity.toPrescription(): Prescription {
        val items = try {
            prescriptionDao.getPrescriptionItems(this.localId).map { itemEntity ->
                val medication = prescriptionDao.getMedicationById(itemEntity.medicationId)
                    ?: MedicationEntity(
                        id = itemEntity.medicationId,
                        name = "Unknown Medication",
                        description = null,
                        created_at = null,
                        updated_at = null
                    )

                PrescriptionItem(
                    id = itemEntity.id ?: -1,
                    prescription = this.id ?: -1,
                    medication = Medication(
                        id = medication.id,
                        name = medication.name,
                        description = medication.description,
                        created_at = medication.created_at,
                        updated_at = medication.updated_at
                    ),
                    dosage = itemEntity.dosage,
                    duration = itemEntity.duration,
                    frequency = itemEntity.frequency,
                    instructions = itemEntity.instructions
                )
            }
        } catch (e: Exception) {
            Log.e("PrescriptionRepository", "Error getting prescription items: ${e.message}", e)
            emptyList()
        }

        return Prescription(
            id = this.id ?: -1,
            patient = this.patient,
            doctor = this.doctor,
            date = this.date,
            notes = this.notes,
            items = items,
            pdf_file = this.pdfFile,
            created_at = this.createdAt,
            updated_at = this.updatedAt
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: PrescriptionRepository? = null

        fun getInstance(context: Context): PrescriptionRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = PrescriptionRepository(
                    apiService = com.example.data.network.ApiClient.apiService,
                    appDatabase = AppDatabase.getDatabase(context),
                    context = context
                )
                INSTANCE = instance
                instance
            }
        }
    }
}
