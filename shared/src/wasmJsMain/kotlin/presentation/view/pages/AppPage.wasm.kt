package presentation.view.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.core.context.GlobalContext
import presentation.viewmodels.AuthViewModel
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel

@Composable
actual fun provideChatBotViewModel(): ChatBotViewModel {
    return GlobalContext.get().get()
}

@Composable
actual fun provideAuthViewModel(): AuthViewModel {
    return GlobalContext.get().get()
}

@Composable
actual fun provideMenuViewModel(): MenuViewModel {
    return GlobalContext.get().get()
}

@Composable
actual fun NavigationMain() {
    var currentScreen by remember { mutableStateOf("login") }
    val chatBotVm = remember { GlobalContext.get().get<ChatBotViewModel>() }
    val menuVm    = remember { GlobalContext.get().get<MenuViewModel>() }
    val authVm    = remember { GlobalContext.get().get<AuthViewModel>() }

    when (currentScreen) {
        "login" -> LoginPage(onLoginSuccess = { currentScreen = "menu" })

        "menu" -> MenuPage(
            onNavigateToSection = { section ->
                currentScreen = if (section == "ChatBot") "chatbot" else "development"
            },
            onLogout = {
                authVm.logout()
                currentScreen = "login"
            }
        )

        "chatbot" -> ChatBotPage(
            viewModel           = chatBotVm,
            menuViewModel       = menuVm,
            onNavigateToSection = { section ->
                currentScreen = if (section == "ChatBot") "chatbot" else "menu"
            },
            onLogout = {
                authVm.logout()
                currentScreen = "login"
            }
        )

        "development" -> DevelopmentScreen(onBack = { currentScreen = "menu" })
    }
}

@Composable
private fun DevelopmentScreen(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text("🚧 En Desarrollo", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Esta función estará disponible próximamente.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBack) { Text("← Volver") }
        }
    }
}
