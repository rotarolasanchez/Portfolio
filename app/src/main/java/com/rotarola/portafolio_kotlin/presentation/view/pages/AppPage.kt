package com.rotarola.portafolio_kotlin.presentation.view.pages

import MenuTemplate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/*
@Composable
fun NavigationMain() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginPage(
                onLoginSuccess = {
                    navController.navigate("menu") {
                        // Limpia el back stack para que no se pueda volver al login
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("menu") {
            MenuPage(
                onNavigateToSection = { section ->
                    // Aquí puedes manejar la navegación a las diferentes secciones
                    when (section) {
                        "Home" -> { ScanPage() }
                        "Profile" -> { /* navegar a perfil */ }
                        "Settings" -> { /* navegar a configuración */ }
                        "Help" -> { /* navegar a ayuda */ }
                    }
                }
            )
        }
    }
}*/



@Composable
fun NavigationMain() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginPage(
                onLoginSuccess = {
                    navController.navigate("menu") {
                        // Limpia el back stack para que no se pueda volver al login
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("menu") {
            MenuPage(
                onNavigateToSection = { section ->
                    // Aquí navegas a las diferentes rutas
                    when (section) {
                        "Home" -> navController.navigate("scan")
                        "Profile" -> navController.navigate("profile")
                        "Settings" -> navController.navigate("settings")
                        "Help" -> navController.navigate("help")
                    }
                }
            )
        }

        // Agregar las rutas para cada sección
        composable("scan") {
            ScanPage(
                /*onNavigateBack = {
                    navController.popBackStack()
                }*/
            )
        }

        /*composable("profile") {
            ProfilePage(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("settings") {
            SettingsPage(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("help") {
            HelpPage(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }*/
    }
}