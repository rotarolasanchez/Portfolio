package com.rotarola.portafolio_kotlin.presentation.view.templates

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.rotarola.portafolio_kotlin.presentation.state.toCameraUiState
import com.rotarola.portafolio_kotlin.presentation.view.organisms.CameraScreen
import com.rotarola.portafolio_kotlin.presentation.view.organisms.ChatScreen
import com.rotarola.portafolio_kotlin.presentation.viewmodels.ChatBotViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatBotTemplate(viewModel: ChatBotViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold {
        when {
            uiState.showCamera -> {
                CameraScreen(
                    uiState = uiState.toCameraUiState(),
                    onImageCaptured = viewModel::processImage,
                    onRetakePhoto = viewModel::hideCamera
                )
            }
            else -> {
                ChatScreen(
                    uiState = uiState,
                    onSendMessage = viewModel::sendMessage,
                    onCameraClick = viewModel::showCamera
                )
            }
        }
    }
}