package com.example.ecare_mobile

import  com.example.doctorlisting.AppNavigation

// androidApp/src/main/java/com/example/androidApp/MainActivity.kt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation() // Use the extracted navigation composable
        }
    }
}