package com.example.ecare_mobile

import DoctorListScreen
import  com.example.doctorlisting.AppNavigation

// androidApp/src/main/java/com/example/androidApp/MainActivity.kt
import  com.example.patientprofile.ui.theme.screens.ProfileScreen
import  com.example.patientprofile.ui.theme.screens.PersonalInfoScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.DoctorRepository
import com.example.data.repository.UserRepository
import com.example.doctorlisting.AppNavigation
import com.example.patientprofile.ui.theme.screens.DoctorPersonalInfoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            val navController = rememberNavController()
//            val userRepository = UserRepository() // Initialize your repository here
// val doctorRepository = DoctorRepository()

            DoctorListScreen()
        }
    }
}