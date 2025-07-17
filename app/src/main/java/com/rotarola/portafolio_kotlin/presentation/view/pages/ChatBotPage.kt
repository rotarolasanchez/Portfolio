package com.rotarola.portafolio_kotlin.presentation.view.pages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rotarola.portafolio_kotlin.presentation.viewmodels.ScanState
import com.rotarola.portafolio_kotlin.presentation.viewmodels.ScanViewModel
import java.io.File
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.rememberCoroutineScope
import com.rotarola.portafolio_kotlin.core.utils.GeminiService
import com.rotarola.portafolio_kotlin.core.utils.correctImageOrientation
import com.rotarola.portafolio_kotlin.core.utils.cropBitmapToGuideRect
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import com.rotarola.portafolio_kotlin.presentation.view.templates.ChatBotTemplate

@Composable
fun ChatBotPage(viewModel: ScanViewModel = hiltViewModel()) {

    ChatBotTemplate(viewModel)

}






@Composable
fun ResizeCorner(
    modifier: Modifier = Modifier,
    onDrag: (Offset) -> Unit
) {
    Box(
        modifier = modifier
            .then(
                Modifier
                    .size(20.dp)
                    .background(Color.Green, RoundedCornerShape(10.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onDrag(dragAmount)
                        }
                    }
            )
    )
}

@Composable
fun CameraPreviewWithCapture(
    onImageCaptureReady: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    ) { view ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also {
                    it.setSurfaceProvider(view.surfaceProvider)
                }

            val imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            onImageCaptureReady(imageCapture)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Error binding use cases", e)
            }

        }, ContextCompat.getMainExecutor(context))
    }
}

@Composable
fun CropImageScreen(
    originalBitmap: Bitmap,
    onCropConfirmed: (Bitmap) -> Unit
) {
    val imageWidth = originalBitmap.width.toFloat()
    val imageHeight = originalBitmap.height.toFloat()

    var cropRect by remember {
        mutableStateOf(
            Rect(
                imageWidth * 0.1f,
                imageHeight * 0.2f,
                imageWidth * 0.9f,
                imageHeight * 0.8f
            )
        )
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragCorner by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            bitmap = originalBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val cornerSize = 30f
                            val corners = mapOf(
                                "topLeft" to Rect(cropRect.left - cornerSize/2, cropRect.top - cornerSize/2, cropRect.left + cornerSize/2, cropRect.top + cornerSize/2),
                                "topRight" to Rect(cropRect.right - cornerSize/2, cropRect.top - cornerSize/2, cropRect.right + cornerSize/2, cropRect.top + cornerSize/2),
                                "bottomLeft" to Rect(cropRect.left - cornerSize/2, cropRect.bottom - cornerSize/2, cropRect.left + cornerSize/2, cropRect.bottom + cornerSize/2),
                                "bottomRight" to Rect(cropRect.right - cornerSize/2, cropRect.bottom - cornerSize/2, cropRect.right + cornerSize/2, cropRect.bottom + cornerSize/2)
                            )

                            dragCorner = corners.entries.find { (_, rect) ->
                                rect.contains(offset)
                            }?.key

                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                            dragCorner = null
                        }
                    ) { change, dragAmount ->
                        change.consume()

                        when (dragCorner) {
                            "topLeft" -> {
                                cropRect = Rect(
                                    (cropRect.left + dragAmount.x).coerceIn(0f, cropRect.right - 50f),
                                    (cropRect.top + dragAmount.y).coerceIn(0f, cropRect.bottom - 50f),
                                    cropRect.right,
                                    cropRect.bottom
                                )
                            }
                            "topRight" -> {
                                cropRect = Rect(
                                    cropRect.left,
                                    (cropRect.top + dragAmount.y).coerceIn(0f, cropRect.bottom - 50f),
                                    (cropRect.right + dragAmount.x).coerceIn(cropRect.left + 50f, imageWidth),
                                    cropRect.bottom
                                )
                            }
                            "bottomLeft" -> {
                                cropRect = Rect(
                                    (cropRect.left + dragAmount.x).coerceIn(0f, cropRect.right - 50f),
                                    cropRect.top,
                                    cropRect.right,
                                    (cropRect.bottom + dragAmount.y).coerceIn(cropRect.top + 50f, imageHeight)
                                )
                            }
                            "bottomRight" -> {
                                cropRect = Rect(
                                    cropRect.left,
                                    cropRect.top,
                                    (cropRect.right + dragAmount.x).coerceIn(cropRect.left + 50f, imageWidth),
                                    (cropRect.bottom + dragAmount.y).coerceIn(cropRect.top + 50f, imageHeight)
                                )
                            }
                            else -> {
                                // Mover todo el rectángulo
                                if (cropRect.contains(change.position)) {
                                    val newRect = cropRect.translate(dragAmount.x, dragAmount.y)
                                    if (newRect.left >= 0 && newRect.right <= imageWidth &&
                                        newRect.top >= 0 && newRect.bottom <= imageHeight) {
                                        cropRect = newRect
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            // Overlay oscuro
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset.Zero,
                size = Size(size.width, size.height)
            )

            // Área de recorte transparente
            drawRect(
                color = Color.Transparent,
                topLeft = Offset(cropRect.left, cropRect.top),
                size = Size(cropRect.width, cropRect.height),
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
            )

            // Borde del área de recorte
            drawRect(
                color = Color.Green,
                topLeft = Offset(cropRect.left, cropRect.top),
                size = Size(cropRect.width, cropRect.height),
                style = Stroke(width = 4f)
            )

            // Esquinas redimensionables
            val cornerSize = 20f
            val corners = listOf(
                Offset(cropRect.left - cornerSize/2, cropRect.top - cornerSize/2),
                Offset(cropRect.right - cornerSize/2, cropRect.top - cornerSize/2),
                Offset(cropRect.left - cornerSize/2, cropRect.bottom - cornerSize/2),
                Offset(cropRect.right - cornerSize/2, cropRect.bottom - cornerSize/2)
            )

            corners.forEach { corner ->
                drawRect(
                    color = Color.Green,
                    topLeft = corner,
                    size = Size(cornerSize, cornerSize),
                    style = Fill
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        // Cancelar y volver a la cámara
                        onCropConfirmed(originalBitmap)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        val cropped = Bitmap.createBitmap(
                            originalBitmap,
                            cropRect.left.toInt().coerceIn(0, originalBitmap.width - 1),
                            cropRect.top.toInt().coerceIn(0, originalBitmap.height - 1),
                            cropRect.width.toInt().coerceAtMost(originalBitmap.width - cropRect.left.toInt()),
                            cropRect.height.toInt().coerceAtMost(originalBitmap.height - cropRect.top.toInt())
                        )
                        onCropConfirmed(cropped)
                    }
                ) {
                    Text("Recortar y analizar")
                }
            }
        }
    }
}







