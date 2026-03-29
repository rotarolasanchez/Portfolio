package presentation.view.pages

import androidx.compose.runtime.Composable
import org.koin.core.context.GlobalContext
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.AuthViewModel
import presentation.viewmodels.MenuViewModel

@Composable
actual fun provideChatBotViewModel(): ChatBotViewModel {
    return GlobalContext.get().get<ChatBotViewModel>()
}

@Composable
actual fun provideAuthViewModel(): AuthViewModel {
    return GlobalContext.get().get<AuthViewModel>()
}

@Composable
actual fun provideMenuViewModel(): MenuViewModel {
    return GlobalContext.get().get<MenuViewModel>()
}

