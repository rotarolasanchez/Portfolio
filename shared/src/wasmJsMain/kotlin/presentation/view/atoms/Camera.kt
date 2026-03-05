package presentation.view.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

actual class PlatformImageCapture

@Composable
actual fun CameraPreviewWithCapture(
    onImageCaptureReady: (PlatformImageCapture) -> Unit,
    modifier: Modifier
) {
    var isCameraActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📷",
            fontSize = 64.sp,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isCameraActive) "Cámara Web Activa" else "Funcionalidad de Cámara Web",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "En desarrollo: Integración con MediaDevices API",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    isCameraActive = !isCameraActive
                    onImageCaptureReady(PlatformImageCapture())
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCameraActive) Color.Red else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isCameraActive) "Detener" else "Iniciar Cámara")
            }

            if (isCameraActive) {
                Button(
                    onClick = { /* Capturar foto */ }
                ) {
                    Text("Capturar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Text(
                text = "💡 Tip: Para usar la cámara real en web, se necesita implementar:\n" +
                        "• navigator.mediaDevices.getUserMedia()\n" +
                        "• Canvas API para captura\n" +
                        "• File API para procesamiento",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
