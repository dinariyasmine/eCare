package com.example.ecare_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authentification.screen.ui.screen.*
import com.example.core.theme.ECareMobileTheme
import com.example.data.repository.AuthRepository
import com.example.data.retrofit.RetrofitInstance
import com.example.data.viewModel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.data.util.TokenManager
import com.example.doctorlisting.ui.screen.DoctorDetailScreen
import com.example.doctorlisting.ui.screen.DoctorFeedbackScreen
import com.example.doctorlisting.ui.screen.DoctorListScreen
import com.example.doctorlisting.ui.screen.DoctorReviewsScreen
import com.example.doctorlisting.ui.screen.HomeScreen
import com.example.patientprofile.ui.theme.screens.DoctorProfileScreen
import com.example.patientprofile.ui.theme.screens.Doctorparams
import com.example.patientprofile.ui.theme.screens.PatientProfileScreen
import com.example.patientprofile.ui.theme.screens.Patientparams

class MainActivity : ComponentActivity() {
    private lateinit var googleAuthHelper: googleAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(applicationContext)

        // Initialize Google Auth Helper
        googleAuthHelper = googleAuthHelper(this)

        setContent {
            ECareMobileTheme {
                var showSplash by remember { mutableStateOf(true) }

                // Show splash screen first, then automatically transition to SignIn
                if (showSplash) {
                    SplashScreen(onSplashComplete = { showSplash = false })
                } else {
                    MainAppContent(googleAuthHelper)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(googleAuthHelper: googleAuthHelper) {
    val navController = rememberNavController()

    // Create repository and ViewModel at the app level to share between screens
    val authRepository = remember { AuthRepository(RetrofitInstance.apiService) }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Companion.Factory(authRepository)
    )

    // NavHost with SignIn as the start destination
    NavHost(
        navController = navController,
        startDestination = Routes.SIGN_IN
    ) {
        composable(Routes.SIGN_IN) {
            LoginScreen(googleAuthHelper = googleAuthHelper, navController = navController)
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(googleAuthHelper = googleAuthHelper, navController = navController)
        }
        composable(Routes.SIGN_UP2) {
            SignUp2Screen(
                googleAuthHelper = googleAuthHelper,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // Forgot Password screen
        composable(Routes.FORGOT_PASS) {
            ForgotPass(navController = navController, viewModel = authViewModel)
        }

        // OTP Verification screen
        composable(
            "${Routes.OTP}/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OTPScreen(
                navController = navController,
                email = email,
                authViewModel = authViewModel
            )
        }

        // Reset Password screen - Modified to match the parameters in your code
        composable(
            "${Routes.RESET_PASS}/{email}/{otpCode}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otpCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val otpCode = backStackEntry.arguments?.getString("otpCode") ?: ""

            ResetPasswordScreen(
                navController = navController,
                authViewModel = authViewModel,
                email = email,
                otpCode = otpCode
            )
        }

        composable(Routes.HOME) {
            HomePage(navController = navController)
        }
        composable(Routes.DOCTOR_PARAMS) {
            Doctorparams(navController = navController)
        }

        composable(Routes.PATIENT_PARAMS) {
            Patientparams(navController = navController)
        }

        composable("patient_profile/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")?.toIntOrNull()
            if (patientId != null) {
                PatientProfileScreen(patientId = patientId)
            }
        }

        composable("doctor_profile/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")?.toIntOrNull()
            if (doctorId != null) {
                DoctorProfileScreen(
                    doctorId = doctorId,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }


        }}