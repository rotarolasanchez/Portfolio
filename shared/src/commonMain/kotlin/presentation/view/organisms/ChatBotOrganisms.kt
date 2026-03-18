package presentation.view.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.outline_photo_camera_24
import portafolio_kotlin.shared.generated.resources.outline_send_24
import presentation.state.ChatBotUiState
import presentation.view.atoms.ChatBotEditText
import presentation.view.molecules.ChatMessageBubble

// ChatScreen - Composable multiplataforma
@Composable
fun ChatScreen(
    uiState: ChatBotUiState,
    onSendMessage: (String) -> Unit,
    onCameraClick: () -> Unit
) {
    var userInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Lista de mensajes en un container responsivo
            Box(
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.messages.asReversed()) { message ->
                        ChatMessageBubble(message)
                    }
                }
            }

            // Indicador de escritura responsivo
            if (uiState.isProcessing) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Procesando...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Mensaje de error responsivo
            uiState.error?.let { error ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Input responsivo
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
            ) {
                ChatBotEditText(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = "Escribe tu mensaje...",
                    leadingIcon = Res.drawable.outline_photo_camera_24,
                    trailingIcon = Res.drawable.outline_send_24,
                    leadingIconStatus = true,
                    trailingIconStatus = userInput.isNotBlank(),
                    leadingIconOnClick = onCameraClick,
                    trailingIconOnClick = {
                        if (userInput.isNotBlank()) {
                            onSendMessage(userInput)
                            userInput = ""
                        }
                    },
                    countMaxCharacter = 200,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

// Tipo común para imagen capturada - cada plataforma lo implementa
expect class PlatformBitmap

// Funciones expect para cámara - implementadas en cada plataforma
@Composable
expect fun CameraWithOverlaySection(
    showOverlay: Boolean,
    onShowOverlayChange: (Boolean) -> Unit,
    onImageCaptured: (PlatformBitmap) -> Unit
)

@Composable
expect fun ImagePreviewScreen(
    bitmap: PlatformBitmap,
    onAccept: () -> Unit,
    onRetake: () -> Unit
)

@Composable
expect fun CameraScreen(
    uiState: presentation.state.CameraUiState,
    onImageCaptured: (PlatformBitmap) -> Unit,
    onRetakePhoto: () -> Unit
)
