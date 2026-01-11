package com.rotarola.portafolio_kotlin.presentation.view.pages

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.rotarola.portafolio_kotlin.presentation.viewmodels.ChatBotViewModel
import com.rotarola.portafolio_kotlin.presentation.view.templates.ChatBotTemplate

@Composable
fun ChatBotPage(viewModel: ChatBotViewModel = hiltViewModel()) {

    ChatBotTemplate(viewModel)

}





