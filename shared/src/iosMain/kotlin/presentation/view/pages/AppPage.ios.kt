package presentation.view.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import di.IosViewModelHolder
import presentation.viewmodels.AuthViewModel
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel

@Composable
actual fun provideChatBotViewModel(): ChatBotViewModel =
    remember { IosViewModelHolder.chatBotViewModel!! }

@Composable
actual fun provideAuthViewModel(): AuthViewModel =
    remember { IosViewModelHolder.authViewModel!! }

@Composable
actual fun provideMenuViewModel(): MenuViewModel =
    remember { IosViewModelHolder.menuViewModel!! }

/**
 * Navegación iOS con state machine — no usa navigation-compose para evitar
 * el crash de la versión alpha (Trace.uikit.kt / _NSCFNumber exception).
 */
@Composable
actual fun NavigationMain() {
    var currentScreen by remember { mutableStateOf("login") }

    when (currentScreen) {
        "login" -> LoginPage(
            onLoginSuccess = { currentScreen = "menu" }
        )

        "menu" -> MenuPage(
            onNavigateToSection = { section ->
                when (section) {
                    "ChatBot" -> currentScreen = "chatbot"
                    else      -> currentScreen = "development"
                }
            },
            onLogout = {
                IosViewModelHolder.authViewModel?.logout()
                currentScreen = "login"
            }
        )

        "chatbot" -> {
            val vm     = IosViewModelHolder.chatBotViewModel ?: return
            val menuVm = IosViewModelHolder.menuViewModel    ?: return
            ChatBotPage(
                viewModel           = vm,
                menuViewModel       = menuVm,
                onNavigateToSection = { section ->
                    currentScreen = if (section == "ChatBot") "chatbot" else "menu"
                },
                onLogout = {
                    IosViewModelHolder.authViewModel?.logout()
                    currentScreen = "login"
                }
            )
        }

        "development" -> DevelopmentScreen(
            onBack = { currentScreen = "menu" }
        )
    }
}

@Composable
private fun DevelopmentScreen(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text("🚧 En desarrollo", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "Esta sección estará disponible próximamente.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBack) { Text("← Volver") }
        }
    }
}
