package com.rotarola.portafolio_kotlin.presentation.view.templates

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.rotarola.portafolio_kotlin.presentation.view.organisms.CameraWithOverlaySection
import com.rotarola.portafolio_kotlin.presentation.view.organisms.ChatScreen
import com.rotarola.portafolio_kotlin.presentation.view.organisms.ImagePreviewScreen
import com.rotarola.portafolio_kotlin.presentation.viewmodels.ScanState
import com.rotarola.portafolio_kotlin.presentation.viewmodels.ScanViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatBotTemplate(viewModel: ScanViewModel = hiltViewModel()) {

    val scanState by viewModel.scanState.collectAsState()
    var showCameraScreen by remember { mutableStateOf(false) }
    var showChatScreen by remember { mutableStateOf(true) }
    var showPreviewScreen by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var detectedText by remember { mutableStateOf("") }
    var showOverlay by remember { mutableStateOf(true) }

    Scaffold(
        //modifier = modifier,
        snackbarHost = {
        }
    ) {
        when {
            showCameraScreen -> {
                CameraWithOverlaySection(
                    showOverlay = showOverlay,
                    onShowOverlayChange = { newValue -> showOverlay = newValue },
                    onImageCaptured = { bitmap ->
                        capturedBitmap = bitmap
                        showCameraScreen = false
                        showPreviewScreen = true
                    }
                )
            }
            showPreviewScreen && capturedBitmap != null -> {
                ImagePreviewScreen(
                    bitmap = capturedBitmap!!,
                    onAccept = {
                        viewModel.processImage(capturedBitmap!!)
                        showPreviewScreen = false
                        showChatScreen = true
                    },
                    onRetake = {
                        capturedBitmap = null
                        showPreviewScreen = false
                        showCameraScreen = true
                    }
                )
            }
            showChatScreen -> {
                ChatScreen(
                    initialProblem = detectedText,
                    onCameraClick = {
                        showCameraScreen = true
                        showChatScreen = false
                    }
                )
            }
        }

        if (scanState is ScanState.Success) {
            detectedText = (scanState as ScanState.Success).text
            showChatScreen = true
            viewModel.reset()
        }
    }
}