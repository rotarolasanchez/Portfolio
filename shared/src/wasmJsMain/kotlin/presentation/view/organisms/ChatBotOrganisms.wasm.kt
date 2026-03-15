package presentation.view.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import core.interop.pickImageFile
import kotlinx.coroutines.launch
import presentation.state.CameraUiState

// Implementación de PlatformBitmap para Web
actual class PlatformBitmap {
    var imageData: String? = null

    constructor(data: String) {
        this.imageData = data
    }

    constructor()
}

@Composable
actual fun CameraScreen(
    uiState: CameraUiState,
    onImageCaptured: (PlatformBitmap) -> Unit,
    onRetakePhoto: () -> Unit
) {
    var selectedImageData by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageData == null) {
            // Pantalla de selección de imagen
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "📎",
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Adjuntar Imagen",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Selecciona una imagen de tu dispositivo para analizarla con IA",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Cargando imagen...", style = MaterialTheme.typography.bodySmall)
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                val imageBase64 = pickImageFile()
                                isLoading = false
                                if (imageBase64 != null) {
                                    selectedImageData = imageBase64
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.6f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("🖼️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar Imagen")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onRetakePhoto,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        } else {
            // Vista previa de imagen seleccionada
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Imagen seleccionada",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Preview de la imagen
                Card(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🖼️",
                            fontSize = 80.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            selectedImageData?.let { data ->
                                onImageCaptured(PlatformBitmap(data))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("✅ Analizar con IA")
                    }

                    OutlinedButton(
                        onClick = {
                            // Seleccionar otra imagen
                            scope.launch {
                                isLoading = true
                                val imageBase64 = pickImageFile()
                                isLoading = false
                                if (imageBase64 != null) {
                                    selectedImageData = imageBase64
                                } else {
                                    selectedImageData = null
                                }
                            }
                        }
                    ) {
                        Text("🔄 Otra imagen")
                    }

                    OutlinedButton(
                        onClick = {
                            selectedImageData = null
                            onRetakePhoto()
                        }
                    ) {
                        Text("❌ Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
actual fun CameraWithOverlaySection(
    showOverlay: Boolean,
    onShowOverlayChange: (Boolean) -> Unit,
    onImageCaptured: (PlatformBitmap) -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📎 Adjuntar Imagen",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        val imageBase64 = pickImageFile()
                        if (imageBase64 != null) {
                            onImageCaptured(PlatformBitmap(imageBase64))
                        }
                    }
                }
            ) {
                Text("Seleccionar imagen")
            }
        }
    }
}

@Composable
actual fun ImagePreviewScreen(
    bitmap: PlatformBitmap,
    onAccept: () -> Unit,
    onRetake: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Vista previa",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.size(250.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("🖼️", fontSize = 64.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onAccept) {
                    Text("✅ Aceptar")
                }
                OutlinedButton(onClick = onRetake) {
                    Text("🔄 Otra imagen")
                }
            }
        }
    }
}
