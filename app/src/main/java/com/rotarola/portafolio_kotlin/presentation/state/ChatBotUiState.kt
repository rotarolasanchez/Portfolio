package com.rotarola.portafolio_kotlin.presentation.state

import android.graphics.Bitmap
import com.rotarola.portafolio_kotlin.domain.model.ChatMessage

data class ChatBotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isProcessing: Boolean = false,
    val error: String? = null,
    val showCamera: Boolean = false,
    val capturedImage: Bitmap? = null,
    val isLoading: Boolean = false
)

enum class CameraState {
    Preview, ImagePreview
}

data class CameraUiState(
    val currentState: CameraState = CameraState.Preview,
    val showOverlay: Boolean = true,
    val capturedImage: Bitmap? = null,
    val isCapturing: Boolean = false
)

// Extension function para convertir ChatBotUiState a CameraUiState
fun ChatBotUiState.toCameraUiState(): CameraUiState {
    return CameraUiState(
        currentState = if (capturedImage != null) CameraState.ImagePreview else CameraState.Preview,
        showOverlay = true,
        capturedImage = capturedImage,
        isCapturing = isProcessing
    )
}

sealed class ScanState {
    object Initial : ScanState()
    object Processing : ScanState()
    data class Success(val text: String) : ScanState()
    data class Error(val message: String) : ScanState()
}