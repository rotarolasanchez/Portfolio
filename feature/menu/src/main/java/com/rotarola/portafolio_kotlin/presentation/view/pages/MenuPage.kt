package com.rotarola.portafolio_kotlin.presentation.view.pages

import MenuTemplate
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.rotarola.portafolio_kotlin.presentation.viewmodels.MenuViewModel


@Composable
fun MenuPage(onNavigateToSection: (String) -> Unit) {
    val menuViewModel: MenuViewModel = hiltViewModel()
    MenuTemplate(
        viewModel = menuViewModel,
        onNavigateToSection = { section ->
            Log.d("MenuPage", "Navegando a: $section")
            onNavigateToSection(section)
        }
    )
}