package presentation.view.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import presentation.viewmodels.AuthViewModel
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel

@Composable
actual fun provideAuthViewModel(): AuthViewModel = koinViewModel()

@Composable
actual fun provideChatBotViewModel(): ChatBotViewModel = koinViewModel()

@Composable
actual fun provideMenuViewModel(): MenuViewModel = koinViewModel()

@Composable
actual fun NavigationMain() {
    val navController = rememberNavController()
    var hasNavigationError by remember { mutableStateOf(false) }
    var isDevelopmentFeature by remember { mutableStateOf(false) }

    when {
        hasNavigationError -> ErrorScreen(onRetry = { hasNavigationError = false })
        isDevelopmentFeature -> DevelopmentScreen(onBack = { isDevelopmentFeature = false })
        else -> AppNavigation(
            navController = navController,
            onNavigationError = { hasNavigationError = true },
            onDevelopmentFeature = { isDevelopmentFeature = true }
        )
    }
}

@Composable
private fun ErrorScreen(onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            Button(onClick = onRetry) { Text("Reintentar") }
        }
    }
}

@Composable
private fun DevelopmentScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
    navController: NavHostController,
    onNavigationError: () -> Unit,
    onDevelopmentFeature: () -> Unit
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(
                onLoginSuccess = {
                    runCatching {
                        navController.navigate("menu") {
                            popUpTo("login") { inclusive = true }
                        }
                    }.onFailure { println("NavigationMain: Login error - ${it.message}"); onNavigationError() }
                }
            )
        }
        composable("menu") {
            val authVm = provideAuthViewModel()
            MenuPage(
                onNavigateToSection = { section ->
                    runCatching {
                        when (section) {
                            "ChatBot" -> navController.navigate("chatbot")
                            else -> onDevelopmentFeature()
                        }
                    }.onFailure { println("NavigationMain: Nav error - ${it.message}"); onNavigationError() }
                },
                onLogout = {
                    authVm.logout()
                    runCatching {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }.onFailure { println("NavigationMain: Logout nav error - ${it.message}"); onNavigationError() }
                }
            )
        }
        composable("chatbot") {
            val viewModel = provideChatBotViewModel()
            val menuViewModel = provideMenuViewModel()
            val authVm = provideAuthViewModel()
            ChatBotPage(
                viewModel = viewModel,
                menuViewModel = menuViewModel,
                onNavigateToSection = { section ->
                    runCatching {
                        when (section) {
                            "Menu" -> navController.navigate("menu")
                            "ChatBot" -> { /* ya estamos aquí */ }
                            else -> onDevelopmentFeature()
                        }
                    }.onFailure { println("NavigationMain: Chatbot nav error - ${it.message}"); onNavigationError() }
                },
                onLogout = {
                    authVm.logout()
                    runCatching {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }.onFailure { println("NavigationMain: Logout nav error - ${it.message}"); onNavigationError() }
                }
            )
        }
    }
}
