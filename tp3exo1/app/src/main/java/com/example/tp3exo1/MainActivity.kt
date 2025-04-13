package com.example.tp3exo1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tp3exo1.ui.theme.Tp3exo1Theme
import androidx.compose.ui.unit.dp

// Activité principale de l'application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Active le mode edge-to-edge pour utiliser tout l'écran
        setContent {
            Tp3exo1Theme {
                MainApp() // Point d'entrée de l'UI de l'application
            }
        }
    }
}

// Composant principal qui configure la navigation et le Scaffold
@Composable
fun MainApp() {
    val navController = rememberNavController() // Contrôleur de navigation

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) } // Configuration de la barre de navigation inférieure
    ) { innerPadding ->
        // Contenu principal avec padding pour éviter le chevauchement avec la BottomBar
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController) // Graphe de navigation
        }
    }
}

// BottomBar de navigation avec 3 destinations principales
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route // Route actuelle

    // Afficher la BottomBar uniquement sur les écrans principaux (1, 4, 6)
    val routes = listOf("ecran1", "ecran4", "ecran6")
    if (currentRoute in routes) {
        BottomAppBar {
            // Item de navigation pour Ecran 1
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Ecran 1") },
                label = { Text("Ecran 1") },
                selected = currentRoute == "ecran1", // Sélectionné si on est sur l'écran 1
                onClick = {
                    if (currentRoute != "ecran1") {
                        navController.navigate("ecran1") {
                            popUpTo("ecran1") { inclusive = true } // Évite l'empilement des écrans
                        }
                    }
                }
            )
            // Item de navigation pour Ecran 4
            NavigationBarItem(
                icon = { Icon(Icons.Filled.List, contentDescription = "Ecran 4") },
                label = { Text("Ecran 4") },
                selected = currentRoute == "ecran4",
                onClick = {
                    if (currentRoute != "ecran4") {
                        navController.navigate("ecran4") {
                            popUpTo("ecran1") // Revient à la racine puis navigue
                        }
                    }
                }
            )
            // Item de navigation pour Ecran 6
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Settings, contentDescription = "Ecran 6") },
                label = { Text("Ecran 6") },
                selected = currentRoute == "ecran6",
                onClick = {
                    if (currentRoute != "ecran6") {
                        navController.navigate("ecran6") {
                            popUpTo("ecran1") // Revient à la racine puis navigue
                        }
                    }
                }
            )
        }
    }
}

// Configuration des routes de navigation et des destinations
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "ecran1") {
        // Définition des routes pour les 7 écrans

        // Premier flux: Ecran 1 -> 2 -> 3
        composable("ecran1") {
            EcranTemplate(
                numero = 1,
                onButtonClick = { navController.navigate("ecran2") }
            )
        }
        composable("ecran2") {
            EcranTemplate(
                numero = 2,
                onButtonClick = { navController.navigate("ecran3") }
            )
        }
        composable("ecran3") {
            EcranTemplate(
                numero = 3,
                onButtonClick = { navController.popBackStack("ecran1", false) } // Retour à l'écran 1
            )
        }

        // Deuxième flux: Ecran 4 -> 5
        composable("ecran4") {
            EcranTemplate(
                numero = 4,
                onButtonClick = { navController.navigate("ecran5") }
            )
        }
        composable("ecran5") {
            EcranTemplate(
                numero = 5,
                onButtonClick = { navController.popBackStack("ecran4", false) } // Retour à l'écran 4
            )
        }

        // Troisième flux: Ecran 6 -> 7
        composable("ecran6") {
            EcranTemplate(
                numero = 6,
                onButtonClick = { navController.navigate("ecran7") }
            )
        }
        composable("ecran7") {
            EcranTemplate(
                numero = 7,
                onButtonClick = { navController.popBackStack("ecran6", false) } // Retour à l'écran 6
            )
        }
    }
}

// Template réutilisable pour tous les écrans
@Composable
fun EcranTemplate(numero: Int, onButtonClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(), // Occupe tout l'espace disponible
        contentAlignment = Alignment.Center // Centre le contenu
    ) {
        // Colonne verticale pour organiser les éléments
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(text = "Bienvenue sur l'écran $numero") // Message d'accueil

            Button(
                onClick = onButtonClick, // Action du bouton définie en paramètre
            ) {
                // Texte du bouton adapté en fonction de l'écran
                when (numero) {
                    1 -> Text(text = "Aller écran 2")
                    2 -> Text(text = "Aller ecran 3")
                    3 -> Text(text = "Retour écran 1")
                    4 -> Text(text = "Aller écran 5")
                    5 -> Text(text = "Retour   écran 4")
                    6 -> Text(text = "Aller   écran 7")
                    7 -> Text(text = "Retour écran 6")
                }
            }
        }
    }
}

// Aperçu pour visualiser l'application dans l'éditeur
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Tp3exo1Theme {
        MainApp()
    }
}