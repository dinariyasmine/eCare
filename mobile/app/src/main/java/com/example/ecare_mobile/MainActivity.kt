// mobile/app/src/main/java/com/example/ecare_mobile/MainActivity.kt
package com.example.ecare_mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.network.ApiClient
import com.example.data.network.DeviceRegistration
import com.example.data.repository.NotificationRepository
import com.example.data.viewModel.NotificationViewModel
import com.example.ecare_mobile.navigation.AppNavigationHandler
import com.example.notifications.service.ECareFirebaseMessagingService
import com.example.notifications.ui.NotificationsScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed with notifications
            getFirebaseToken()
        } else {
            // Permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Log.e("Firebase", "Error initializing Firebase: ${e.message}")
        }
        ECareFirebaseMessagingService.setNavigationHandler(AppNavigationHandler())

        // Check and request notification permission
        checkNotificationPermission()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ECareApp()
                }
            }
        }
    }


    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    getFirebaseToken()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale if needed
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Permission not required for Android < 13
            getFirebaseToken()
        }
    }

    private fun getFirebaseToken() {
        try {
            if (FirebaseApp.getApps(this).isNotEmpty()) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        // Send token to your server
                        Log.d("FCM Token", token)
                    } else {
                        Log.e("FCM Token", "Failed to get token: ${task.exception?.message}")
                    }
                }
            } else {
                Log.e("Firebase", "Firebase not initialized")
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error getting token: ${e.message}")
        }
    }
}

@Composable
fun ECareApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "notifications") {
        composable("home") {
            Text("Home Screen")
            Button(onClick = { navController.navigate("notifications") }) {
                Text("Go to Notifications")
            }
        }
        composable("notifications") {
            val notificationRepository = NotificationRepository(ApiClient.apiService)
            val notificationViewModel: NotificationViewModel = viewModel {
                NotificationViewModel(notificationRepository)
            }
            NotificationsScreen(viewModel = notificationViewModel)
        }

    }
}