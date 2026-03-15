package presentation.state


import domain.model.ChatBotMessage

data class ChatBotUiState(
    val messages: List<ChatBotMessage> = emptyList(),
    val isProcessing: Boolean = false,
    val error: String? = null,
    val showCamera: Boolean = false,
    val capturedImageBytes: ByteArray? = null, // ✅ Cambiar a ByteArray
    val isLoading: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatBotUiState) return false
        return messages == other.messages &&
                isProcessing == other.isProcessing &&
                error == other.error &&
                showCamera == other.showCamera &&
                capturedImageBytes.contentEquals(other.capturedImageBytes) &&
                isLoading == other.isLoading
    }

    override fun hashCode(): Int {
        return capturedImageBytes?.contentHashCode() ?: 0
    }
}

enum class CameraState {
    Preview, ImagePreview
}

data class CameraUiState(
    val currentState: CameraState = CameraState.Preview,
    val showOverlay: Boolean = true,
    val capturedImageBytes: ByteArray? = null,
    val capturedImage: Any? = null, // PlatformBitmap en cada plataforma
    val isCapturing: Boolean = false
)

// Extension function para convertir ChatBotUiState a CameraUiState
fun ChatBotUiState.toCameraUiState(): CameraUiState {
    return CameraUiState(
        currentState = if (capturedImageBytes != null) CameraState.ImagePreview else CameraState.Preview,
        showOverlay = true,
        capturedImageBytes = capturedImageBytes,
        capturedImage = null,
        isCapturing = isProcessing
    )
}

sealed class ScanState {
    object Initial : ScanState()
    object Processing : ScanState()
    data class Success(val text: String) : ScanState()
    data class Error(val message: String) : ScanState()
}