package com.rotarola.portafolio_kotlin.presentation.viewmodels

import com.rotarola.portafolio_kotlin.feature.menu.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.presentation.state.MenuUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        MenuUiState(
            menuItems = listOf(
                "ChatBot",          // Cambiar "Principal" por "ChatBot"
                "Perfil",
                "Configuración",
                "Ayuda"
            ),
            menuIcons = listOf(
                R.drawable.outline_robot_2_24,      // o cambiar por R.drawable.outline_robot_2_24
                R.drawable.baseline_person_24,
                R.drawable.baseline_settings_24,
                R.drawable.baseline_help_24
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