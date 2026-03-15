package presentation.view.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect class PlatformImageCapture

@Composable
expect fun CameraPreviewWithCapture(
    onImageCaptureReady: (PlatformImageCapture) -> Unit,
    modifier: Modifier = Modifier
)