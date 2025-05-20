// mobile/app/src/main/java/com/example/ecare_mobile/MainActivity.kt
package com.example.ecare_mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Send token to your server
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val deviceInfo = DeviceRegistration(registration_id = token)
                        ApiClient.apiService.registerDevice(deviceInfo)
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
            }
        }
    }
}

@Composable
fun ECareApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            // Your home screen
        }
        composable("notifications") {
            val notificationRepository = NotificationRepository(ApiClient.apiService)
            val notificationViewModel: NotificationViewModel = viewModel {
                NotificationViewModel(notificationRepository)
            }
            NotificationsScreen(viewModel = notificationViewModel)
        }
        // Add other screens as needed
    }
}
