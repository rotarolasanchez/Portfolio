package com.rotarola.portafolio_kotlin.presentation.view.pages

import MenuTemplate
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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