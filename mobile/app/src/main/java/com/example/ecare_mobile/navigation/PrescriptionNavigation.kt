package com.example.prescription.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prescription.ui.screen.CreatePrescriptionScreen
import com.example.prescription.ui.screen.PrescriptionDetailScreen
import com.example.prescription.ui.screen.PrescriptionListScreen

object PrescriptionDestinations {
    const val PRESCRIPTION_LIST = "prescription_list"
    const val PRESCRIPTION_DETAIL = "prescription_detail/{prescriptionId}"
    const val CREATE_PRESCRIPTION = "create_prescription/{patientId}"

    fun prescriptionDetailRoute(prescriptionId: Int) = "prescription_detail/$prescriptionId"
    fun createPrescriptionRoute(patientId: Int) = "create_prescription/$patientId"
}

@Composable
fun PrescriptionNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = PrescriptionDestinations.PRESCRIPTION_LIST,
    isDoctor: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(PrescriptionDestinations.PRESCRIPTION_LIST) {
            PrescriptionListScreen(
                navigateToPrescriptionDetail = { prescriptionId ->
                    navController.navigate(PrescriptionDestinations.prescriptionDetailRoute(prescriptionId))
                },
                navigateToCreatePrescription = {
                    // This would typically navigate to a patient selection screen first
                    // For simplicity, we'll assume we have a patient ID
                    navController.navigate(PrescriptionDestinations.createPrescriptionRoute(1))
                },
                isDoctor = isDoctor
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
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(
            PrescriptionDestinations.CREATE_PRESCRIPTION,
            arguments = listOf(
                navArgument("patientId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: 0
            CreatePrescriptionScreen(
                patientId = patientId,
                navigateBack = { navController.popBackStack() },
                navigateToPrescriptionDetail = { prescriptionId ->
                    navController.navigate(PrescriptionDestinations.prescriptionDetailRoute(prescriptionId)) {
                        popUpTo(PrescriptionDestinations.PRESCRIPTION_LIST)
                    }
                }
            )
        }
    }
}
