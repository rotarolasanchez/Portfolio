package com.rotarola.portafolio_kotlin.presentation.state

import com.rotarola.portafolio_kotlin.R

data class MenuUiState(
    val menuItems: List<String> = listOf("Home", "Profile", "Settings", "Help"),
    val menuIcons: List<Int> = listOf(
        R.drawable.baseline_add_home_24,
        R.drawable.baseline_person_24,
        R.drawable.baseline_settings_24,
        R.drawable.baseline_help_24
    ),
    val selectedItem: String = "Home"
)