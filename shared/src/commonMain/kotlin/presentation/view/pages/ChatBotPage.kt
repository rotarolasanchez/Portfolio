package presentation.view.pages

import androidx.compose.runtime.Composable
import presentation.view.templates.ChatBotTemplate
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel

@Composable
fun ChatBotPage(
    viewModel: ChatBotViewModel,
    menuViewModel: MenuViewModel,
    onNavigateToSection: (String) -> Unit = {}

) {
    ChatBotTemplate(viewModel, menuViewModel, onNavigateToSection)

}





