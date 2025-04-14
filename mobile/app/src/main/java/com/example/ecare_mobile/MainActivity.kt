package com.example.ecare_mobile

import  com.example.doctorlisting.AppNavigation

// androidApp/src/main/java/com/example/androidApp/MainActivity.kt
import  com.example.patientprofile.ui.theme.screens.ProfileScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.UserRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val userRepository = UserRepository() // Initialize your repository here

            ProfileScreen(
                navController = navController,
                userRepository = userRepository
            )
          //  AppNavigation() // Use the extracted navigation composable
        }
    }
}