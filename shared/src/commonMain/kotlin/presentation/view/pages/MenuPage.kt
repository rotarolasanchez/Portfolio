package presentation.view.pages

import androidx.compose.runtime.Composable
import presentation.view.templates.MenuTemplate
import presentation.viewmodels.MenuViewModel


@Composable
fun MenuPage(
    onNavigateToSection: (String) -> Unit,
    onLogout: () -> Unit
) {
    val menuViewModel = provideMenuViewModel()
    MenuTemplate(
        viewModel = menuViewModel,
        onNavigateToSection = { section ->
            println("MenuPage: Navegando a: $section")
            onNavigateToSection(section)
        },
        onLogout = onLogout
    )
}