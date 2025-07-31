package com.rotarola.portafolio_kotlin.presentation.view.pages

import MenuTemplate
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            // Error crítico de navegación
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
                    Button(
                        onClick = {
                            hasNavigationError = false
                        }
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }

        isDevelopmentFeature -> {
            // Pantalla amigable para características en desarrollo
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Usar Lottie si está disponible, o un ícono
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
                    Button(
                        onClick = {
                            isDevelopmentFeature = false
                        }
                    ) {
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

        else -> {
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
                                hasNavigationError = true
                            }
                        }
                    )
                }

                composable("menu") {
                    MenuPage(
                        onNavigateToSection = { section ->
                            runCatching {
                                when (section) {
                                    "Principal" -> {
                                        // Mostrar pantalla de desarrollo
                                        isDevelopmentFeature = true
                                    }
                                    "Perfil" -> {
                                        isDevelopmentFeature = true
                                    }
                                    "Configuración" -> {
                                        isDevelopmentFeature = true
                                    }
                                    "Ayuda" -> {
                                        isDevelopmentFeature = true
                                    }
                                    else -> {
                                        Log.w("NavigationMain", "Unknown section: $section")
                                        isDevelopmentFeature = true
                                    }
                                }
                            }.onFailure { exception ->
                                Log.e("NavigationMain", "Navigation error for section: $section", exception)
                                hasNavigationError = true
                            }
                        }
                    )
                }
            }
        }
    }
}