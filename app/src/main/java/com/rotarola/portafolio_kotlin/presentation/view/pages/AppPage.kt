package com.rotarola.portafolio_kotlin.presentation.view.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationMain() {
    val navController = rememberNavController()
    var hasNavigationError by remember { mutableStateOf(false) }
    var isDevelopmentFeature by remember { mutableStateOf(false) }

    when {
        hasNavigationError -> {
            ErrorScreen(
                onRetry = { hasNavigationError = false }
            )
        }

        isDevelopmentFeature -> {
            DevelopmentScreen(
                onBack = { isDevelopmentFeature = false }
            )
        }

        else -> {
            AppNavigation(
                navController = navController,
                onNavigationError = { hasNavigationError = true },
                onDevelopmentFeature = { isDevelopmentFeature = true }
            )
        }
    }
}

@Composable
private fun ErrorScreen(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error de navegación",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Algo salió mal. Intenta nuevamente.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun DevelopmentScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "En desarrollo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "🚧 En Desarrollo",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Esta función estará disponible pronto",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Gracias por tu paciencia 😊",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver al Menú")
            }
        }
    }
}

@Composable
private fun AppNavigation(
    navController: androidx.navigation.NavHostController,
    onNavigationError: () -> Unit,
    onDevelopmentFeature: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginPage(
                onLoginSuccess = {
                    runCatching {
                        navController.navigate("menu") {
                            popUpTo("login") { inclusive = true }
                        }
                    }.onFailure { exception ->
                        Log.e("NavigationMain", "Login navigation error", exception)
                        onNavigationError()
                    }
                }
            )
        }

        composable("menu") {
            MenuPage(
                onNavigateToSection = { section ->
                    runCatching {
                        when (section) {
                            "ChatBot" -> navController.navigate("chatbot")
                            "Principal" -> onDevelopmentFeature()
                            "Perfil" -> onDevelopmentFeature()
                            "Configuración" -> onDevelopmentFeature()
                            "Ayuda" -> onDevelopmentFeature()
                            else -> {
                                Log.w("NavigationMain", "Unknown section: $section")
                                onDevelopmentFeature()
                            }
                        }
                    }.onFailure { exception ->
                        Log.e("NavigationMain", "Navigation error for section: $section", exception)
                        onNavigationError()
                    }
                }
            )
        }

        composable("chatbot") {
            ChatBotPage()
        }
    }
}