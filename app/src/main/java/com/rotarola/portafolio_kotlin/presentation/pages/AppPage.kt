package com.rotarola.portafolio_kotlin.presentation.pages

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feature_login.presentation.view.pages.LoginPage
import com.example.feature_menu.presentation.templates.MenuTemplate

@OptIn(ExperimentalMaterial3Api::class)
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
                    // Navega a la página del menú
                    navController.navigate("menu")
                }
            )
        }
        composable("menu") {
            MenuTemplate()
        }
    }
}