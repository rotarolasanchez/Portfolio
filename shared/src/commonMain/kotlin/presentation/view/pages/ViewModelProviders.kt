package presentation.view.pages

import androidx.compose.runtime.Composable
import presentation.viewmodels.AuthViewModel
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel

/**
 * Funciones expect para obtener los ViewModels - cada plataforma las implementa
 */

@Composable
expect fun provideAuthViewModel(): AuthViewModel

@Composable
expect fun provideChatBotViewModel(): ChatBotViewModel

@Composable
expect fun provideMenuViewModel(): MenuViewModel

/**
 * Función expect para la navegación principal.
 * iOS usa un estado interno (state machine).
 * Android usa NavHost de navigation-compose.
 * Desktop/Web usan una state machine similar a iOS.
 */
@Composable
expect fun NavigationMain()

