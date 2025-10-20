package com.rotarola.portafolio_kotlin.presentation.view.templates

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.rotarola.portafolio_kotlin.presentation.state.toCameraUiState
import com.rotarola.portafolio_kotlin.presentation.view.organisms.CameraScreen
import com.rotarola.portafolio_kotlin.presentation.view.organisms.CameraWithOverlaySection
import com.rotarola.portafolio_kotlin.presentation.view.organisms.ChatScreen
import com.rotarola.portafolio_kotlin.presentation.view.organisms.ImagePreviewScreen
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