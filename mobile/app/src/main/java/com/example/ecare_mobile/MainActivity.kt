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

class MainActivity : ComponentActivity() {
    private lateinit var googleAuthHelper: googleAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    // NavHost with SignIn as the start destination

    // Add more routes as needed
    NavHost(
        navController = navController,
        startDestination = Routes.SIGN_IN  // or Routes.SIGN_UP, whatever you prefer
    ) {
        composable(Routes.SIGN_IN) {
            LoginScreen(googleAuthHelper = googleAuthHelper, navController = navController)
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(googleAuthHelper = googleAuthHelper, navController = navController)
        }
        composable(Routes.SIGN_UP2) {
            SignUp2Screen(googleAuthHelper = googleAuthHelper, navController = navController)
        }
        composable(Routes.RESET_PASS) {
            resetPass(navController = navController)
        }
        composable(Routes.FORGOT_PASS) {
            ForgotPass(navController = navController)
        }
        composable(Routes.OTP) {
            OTPScreen(navController = navController)
        }
        // Add more routes as needed
    }
}

