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
                        "Home" -> { /* navegar a home */ }
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
    var hasNavigationError by remember { mutableStateOf(false) }

    if (hasNavigationError) {
        // UI de fallback sin try-catch
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error de navegación")
                Button(
                    onClick = {
                        hasNavigationError = false
                        // Reintentar navegación
                    }
                ) {
                    Text("Reintentar")
                }
            }
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = "menu"
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
                        // Manejo de navegación sin try-catch
                        runCatching {
                            navController.navigate(section)
                            when (section) {
                                "Home" -> { /* navegar a home */ }
                                "Profile" -> { /* navegar a perfil */ }
                                "Settings" -> { /* navegar a configuración */ }
                                "Help" -> { /* navegar a ayuda */ }
                            }
                        }.onFailure { exception ->
                            Log.e("MainNavigation", "Navigation error", exception)
                            hasNavigationError = true
                        }
                    }
                )
            }
        }
    }
}
