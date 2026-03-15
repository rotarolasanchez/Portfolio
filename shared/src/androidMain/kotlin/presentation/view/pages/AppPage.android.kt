package presentation.view.pages

import androidx.compose.runtime.Composable
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


