
package com.example.doctorlisting
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doctorlisting.ui.screen.DoctorDetailScreen
import com.example.doctorlisting.ui.screen.DoctorListScreen
import com.example.doctorlisting.ui.screen.DoctorReviewsScreen
import com.example.doctorlisting.ui.screen.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Définir les items de la barre de navigation
    val navigationItems = listOf(
        NavigationItem("home", "Accueil", Icons.Filled.Home),
        NavigationItem("doctors", "Médecins", Icons.Filled.Person),
        NavigationItem("favorites", "Favoris", Icons.Filled.Favorite)
    )

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navigationItems.forEach { item ->
                    BottomNavigationItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Éviter les multiples copies de la même destination
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Éviter les multiples instances de la même destination
                                launchSingleTop = true
                                // Restaurer l'état lors de la navigation
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "doctors",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController = navController)
            }

            composable("doctors") {
                DoctorListScreen(navController = navController)
            }

            composable(
                "doctor/{doctorId}",
                arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getInt("doctorId")
                DoctorDetailScreen(doctorId = doctorId, navController = navController)
            }

            composable(
                "doctor/{doctorId}/reviews",
                arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getInt("doctorId")
                DoctorReviewsScreen(doctorId = doctorId, navController = navController)
            }

            composable("favorites") {
                // Vous devrez créer cet écran
                Text("Écran des favoris")
            }
        }
    }
}

// Classe pour représenter les éléments de la barre de navigation
data class NavigationItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)