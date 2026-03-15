package presentation.view.organisms

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import core.utils.correctImageOrientation
import core.utils.cropBitmapToGuideRect
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.outline_photo_camera_24
import presentation.state.CameraState
import presentation.state.CameraUiState
import presentation.view.atoms.CameraPreviewWithCapture
import presentation.view.atoms.ChatBotButton
import presentation.view.atoms.ResizeCorner
import java.io.File
import kotlin.math.max
import kotlin.math.min

// Typealias para Bitmap de Android
actual typealias PlatformBitmap = Bitmap

@Composable
actual fun CameraWithOverlaySection(
    showOverlay: Boolean,
    onShowOverlayChange: (Boolean) -> Unit,
    onImageCaptured: (PlatformBitmap) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var rectWidth by remember { mutableStateOf(250.dp) }
    var rectHeight by remember { mutableStateOf(150.dp) }
    var rectOffset by remember { mutableStateOf(Offset.Zero) }
    var previewSize by remember { mutableStateOf(Size.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewWithCapture(
            onImageCaptureReady = { imageCapture = it },
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    previewSize = Size(
                        coordinates.size.width.toFloat(),
                        coordinates.size.height.toFloat()
                    )
                }
        )

        // Header con instrucciones
        if (!isCapturing) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "Enfoca el problema matemático",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Ajusta el área verde para enmarcar el texto",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Overlay con rectángulo guía
        if (previewSize != Size.Zero && showOverlay && !isCapturing) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Overlay semi-transparente
                drawRect(
                    color = Color.Black.copy(alpha = 0.5f),
                    topLeft = Offset.Zero,
                    size = size
                )

                // Calcular posición del rectángulo
                val centerX = size.width / 2f + rectOffset.x * density
                val centerY = size.height / 2f + rectOffset.y * density
                val rectWidthPx = rectWidth.value * density
                val rectHeightPx = rectHeight.value * density

                // Área del rectángulo (transparente)
                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(
                        centerX - rectWidthPx / 2f,
                        centerY - rectHeightPx / 2f
                    ),
                    size = Size(rectWidthPx, rectHeightPx),
                    blendMode = BlendMode.Clear
                )

                // Borde del rectángulo
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(
                        centerX - rectWidthPx / 2f,
                        centerY - rectHeightPx / 2f
                    ),
                    size = Size(rectWidthPx, rectHeightPx),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Box redimensionable
            Box(
                modifier = Modifier
                    .size(rectWidth, rectHeight)
                    .offset(
                        x = (previewSize.width / (2f * density)).dp + rectOffset.x.dp - rectWidth.div(2),
                        y = (previewSize.height / (2f * density)).dp + rectOffset.y.dp - rectHeight.div(2)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            if (previewSize.width > 0 && previewSize.height > 0) {
                                val rectWidthPx = rectWidth.value * density
                                val rectHeightPx = rectHeight.value * density

                                val halfScreenWidth = previewSize.width / 2f
                                val halfScreenHeight = previewSize.height / 2f
                                val halfRectWidth = rectWidthPx / 2f
                                val halfRectHeight = rectHeightPx / 2f

                                val maxOffsetX = max(0f, (halfScreenWidth - halfRectWidth) / density)
                                val maxOffsetY = max(0f, (halfScreenHeight - halfRectHeight) / density)
                                val minOffsetX = -maxOffsetX
                                val minOffsetY = -maxOffsetY

                                val newOffsetX = rectOffset.x + dragAmount.x / density
                                val newOffsetY = rectOffset.y + dragAmount.y / density

                                rectOffset = Offset(
                                    if (maxOffsetX >= minOffsetX) {
                                        newOffsetX.coerceIn(minOffsetX, maxOffsetX)
                                    } else {
                                        0f
                                    },
                                    if (maxOffsetY >= minOffsetY) {
                                        newOffsetY.coerceIn(minOffsetY, maxOffsetY)
                                    } else {
                                        0f
                                    }
                                )
                            }
                        }
                    }
            ) {
                // Esquinas de redimensionamiento
                ResizeCorner(
                    modifier = Modifier.align(Alignment.TopStart),
                    onDrag = { dragAmount ->
                        if (previewSize.width > 0 && previewSize.height > 0) {
                            val maxWidth = min(400f, previewSize.width / density)
                            val maxHeight = min(300f, previewSize.height / density)

                            val newWidth = (rectWidth.value - dragAmount.x / density).coerceIn(100f, maxWidth)
                            val newHeight = (rectHeight.value - dragAmount.y / density).coerceIn(80f, maxHeight)

                            rectWidth = newWidth.dp
                            rectHeight = newHeight.dp
                        }
                    }
                )

                ResizeCorner(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onDrag = { dragAmount ->
                        if (previewSize.width > 0 && previewSize.height > 0) {
                            val maxWidth = min(400f, previewSize.width / density)
                            val maxHeight = min(300f, previewSize.height / density)

                            val newWidth = (rectWidth.value + dragAmount.x / density).coerceIn(100f, maxWidth)
                            val newHeight = (rectHeight.value - dragAmount.y / density).coerceIn(80f, maxHeight)

                            rectWidth = newWidth.dp
                            rectHeight = newHeight.dp
                        }
                    }
                )

                ResizeCorner(
                    modifier = Modifier.align(Alignment.BottomStart),
                    onDrag = { dragAmount ->
                        if (previewSize.width > 0 && previewSize.height > 0) {
                            val maxWidth = min(400f, previewSize.width / density)
                            val maxHeight = min(300f, previewSize.height / density)

                            val newWidth = (rectWidth.value - dragAmount.x / density).coerceIn(100f, maxWidth)
                            val newHeight = (rectHeight.value + dragAmount.y / density).coerceIn(80f, maxHeight)

                            rectWidth = newWidth.dp
                            rectHeight = newHeight.dp
                        }
                    }
                )

                ResizeCorner(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    onDrag = { dragAmount ->
                        if (previewSize.width > 0 && previewSize.height > 0) {
                            val maxWidth = min(400f, previewSize.width / density)
                            val maxHeight = min(300f, previewSize.height / density)

                            val newWidth = (rectWidth.value + dragAmount.x / density).coerceIn(100f, maxWidth)
                            val newHeight = (rectHeight.value + dragAmount.y / density).coerceIn(80f, maxHeight)

                            rectWidth = newWidth.dp
                            rectHeight = newHeight.dp
                        }
                    }
                )
            }
        }

        // Botón de captura
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCapturing) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Capturando imagen...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                ChatBotButton(
                    leadingiconResourceId = Res.drawable.outline_photo_camera_24,
                    text = "Capturar",
                    onClick = {
                        isCapturing = true
                        onShowOverlayChange(false)
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(
                            File.createTempFile("image", ".jpg", context.cacheDir)
                        ).build()

                        imageCapture?.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    output.savedUri?.let { uri ->
                                        try {
                                            val bitmap = BitmapFactory.decodeFile(uri.path)
                                            val correctedBitmap = correctImageOrientation(bitmap, uri.path ?: "")

                                            val croppedBitmap = if (previewSize.width > 0 && previewSize.height > 0) {
                                                cropBitmapToGuideRect(
                                                    bitmap = correctedBitmap,
                                                    rectWidth = rectWidth,
                                                    rectHeight = rectHeight,
                                                    rectOffset = rectOffset,
                                                    previewSize = previewSize,
                                                    density = density
                                                )
                                            } else {
                                                correctedBitmap
                                            }

                                            onImageCaptured(croppedBitmap)
                                        } catch (e: Exception) {
                                            Log.e("CameraCapture", "Error al procesar imagen", e)
                                            output.savedUri?.let { fallbackUri ->
                                                try {
                                                    val fallbackBitmap = BitmapFactory.decodeFile(fallbackUri.path)
                                                    onImageCaptured(fallbackBitmap)
                                                } catch (fallbackError: Exception) {
                                                    Log.e("CameraCapture", "Error en fallback", fallbackError)
                                                }
                                            }
                                        }
                                    }
                                    isCapturing = false
                                    onShowOverlayChange(true)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("CameraCapture", "Error al capturar imagen", exception)
                                    isCapturing = false
                                    onShowOverlayChange(true)
                                }
                            }
                        )
                    }
                )
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Header con título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Vista previa",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = onRetake) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White
                )
            }
        }

        // Imagen capturada
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Imagen capturada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Texto informativo
        Text(
            text = "¿Es correcta la imagen? Se analizará para detectar texto matemático.",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onRetake,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Repetir foto")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Analizar imagen")
            }
        }
    }
}

@Composable
actual fun CameraScreen(
    uiState: CameraUiState,
    onImageCaptured: (PlatformBitmap) -> Unit,
    onRetakePhoto: () -> Unit
) {
    when (uiState.currentState) {
        CameraState.Preview -> {
            CameraWithOverlaySection(
                showOverlay = uiState.showOverlay,
                onShowOverlayChange = { /* No necesario aquí */ },
                onImageCaptured = onImageCaptured
            )
        }
        CameraState.ImagePreview -> {
            (uiState.capturedImage as? PlatformBitmap)?.let { bitmap ->
                ImagePreviewScreen(
                    bitmap = bitmap,
                    onAccept = { onImageCaptured(bitmap) },
                    onRetake = onRetakePhoto
                )
            }
        }
    }
}

