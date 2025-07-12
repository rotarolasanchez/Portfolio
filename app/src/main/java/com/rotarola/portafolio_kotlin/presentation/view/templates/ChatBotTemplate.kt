package com.rotarola.portafolio_kotlin.presentation.view.templates

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.rotarola.portafolio_kotlin.presentation.viewmodels.ScanViewModel

@Composable
fun ChatBotTemplate(viewModel: ScanViewModel = hiltViewModel()) {
    Scaffold(
        //modifier = modifier,
        snackbarHost = {
        }
    ) {

    }
}