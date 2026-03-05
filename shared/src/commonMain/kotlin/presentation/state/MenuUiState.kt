package presentation.state

import org.jetbrains.compose.resources.DrawableResource

data class MenuUiState(
    val menuItems: List<String> = listOf("Home", "Profile", "Settings", "Help"),
    val menuIcons: List<DrawableResource> = emptyList(),
    val selectedItem: String = "Home"
)