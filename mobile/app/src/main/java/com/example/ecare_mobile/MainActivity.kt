package com.example.ecare_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.core.theme.ECareMobileTheme
import com.example.data.repository.MedicationRepository
import com.example.data.repository.PatientRepository
import com.example.data.repository.PrescriptionRepository
import com.example.data.network.ApiClient
import com.example.data.retrofit.RetrofitInstance
import com.example.data.util.TokenManager
import com.example.data.viewModel.MedicationViewModel
import com.example.data.viewModel.PatientViewModel
import com.example.data.viewModel.PrescriptionViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.data.local.database.AppDatabase
import com.example.data.sync.SyncManager
import com.example.prescription.navigation.PrescriptionDestinations
import com.example.prescription.ui.screen.CreatePrescriptionScreen
import com.example.prescription.ui.screen.PrescriptionDetailScreen
import com.example.prescription.ui.screen.PrescriptionListScreen

class MainActivity : ComponentActivity() {
    private lateinit var syncManager: SyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(applicationContext)

        // Initialize database and sync manager
        AppDatabase.getDatabase(applicationContext)
        syncManager = SyncManager(applicationContext)
        syncManager.scheduleSyncWork()

        setContent {
            ECareMobileTheme {
                PrescriptionApp(applicationContext)
            }
        }
    }
}

@Composable
fun PrescriptionApp(applicationContext: android.content.Context) {
    val navController = rememberNavController()

    // Create repositories
    val prescriptionRepository = remember {
        PrescriptionRepository.getInstance(applicationContext)
    }
    val medicationRepository = remember { MedicationRepository(ApiClient.apiService) }
    val patientRepository = remember { PatientRepository(RetrofitInstance.apiService) }

    // Create ViewModels
    val prescriptionViewModel: PrescriptionViewModel = viewModel(
        factory = PrescriptionViewModel.Companion.Factory(prescriptionRepository)
    )
    val medicationViewModel: MedicationViewModel = viewModel(
        factory = MedicationViewModel.Companion.Factory(medicationRepository)
    )
    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModel.Companion.Factory(patientRepository)
    )

    // NavHost with PrescriptionList as the start destination
    NavHost(
        navController = navController,
        startDestination = PrescriptionDestinations.PRESCRIPTION_LIST
    ) {
        // Prescription screens
        composable(PrescriptionDestinations.PRESCRIPTION_LIST) {
            PrescriptionListScreen(
                navigateToPrescriptionDetail = { prescriptionId ->
                    navController.navigate(PrescriptionDestinations.prescriptionDetailRoute(prescriptionId))
                },
                navigateToCreatePrescription = {
                    navController.navigate(PrescriptionDestinations.createPrescriptionRoute(33))
                },
                viewModel = prescriptionViewModel,
                isDoctor = true // Set to true for testing as a doctor
            )
        }

        composable(
            PrescriptionDestinations.PRESCRIPTION_DETAIL,
            arguments = listOf(
                navArgument("prescriptionId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val prescriptionId = backStackEntry.arguments?.getInt("prescriptionId") ?: 0
            PrescriptionDetailScreen(
                prescriptionId = prescriptionId,
                navigateBack = { navController.popBackStack() },
                viewModel = prescriptionViewModel
            )
        }

        composable(
            PrescriptionDestinations.CREATE_PRESCRIPTION,
            arguments = listOf(
                navArgument("patientId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: 33
            CreatePrescriptionScreen(
                patientId = patientId,
                navigateBack = { navController.popBackStack() },
                navigateToPrescriptionDetail = { prescriptionId ->
                    navController.navigate(PrescriptionDestinations.prescriptionDetailRoute(prescriptionId)) {
                        popUpTo(PrescriptionDestinations.PRESCRIPTION_LIST)
                    }
                },
                prescriptionViewModel = prescriptionViewModel,
                patientViewModel = patientViewModel,
                medicationViewModel = medicationViewModel
            )
        }
    }
}
