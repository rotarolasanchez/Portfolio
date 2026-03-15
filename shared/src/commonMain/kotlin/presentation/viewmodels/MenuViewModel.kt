package presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.outline_robot_2_24
import portafolio_kotlin.shared.generated.resources.baseline_account_circle_24
import portafolio_kotlin.shared.generated.resources.baseline_home_24
import presentation.state.MenuUiState

class MenuViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        MenuUiState(
            menuItems = listOf(
                "ChatBot",
                "Perfil",
                "Configuración",
                "Ayuda"
            ),
            menuIcons = listOf(
                Res.drawable.outline_robot_2_24,
                Res.drawable.baseline_account_circle_24,
                Res.drawable.baseline_home_24,
                Res.drawable.baseline_home_24
            ),
            selectedItem = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    fun selectMenuItem(item: String) {
        _uiState.value = _uiState.value.copy(selectedItem = item)
    }

    fun onFabClick() {
        viewModelScope.launch {
            // Implementar acción del FAB aquí
        }
    }
}