package com.rotarola.portafolio_kotlin.presentation.view.pages

import MenuTemplate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


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
                        "ChatBot" -> navController.navigate("chatbot")
                        "Profile" -> navController.navigate("profile")
                        "Settings" -> navController.navigate("settings")
                        "Help" -> navController.navigate("help")
                    }
                }
            )
        }

        // Agregar las rutas para cada sección
        composable("chatbot") {
            ChatBotPage()
        }
    }
}