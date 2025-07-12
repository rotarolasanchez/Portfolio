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
import androidx.compose.ui.Alignment
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

@Composable
fun ChatBotPage(viewModel: ScanViewModel = hiltViewModel()) {
    val scanState by viewModel.scanState.collectAsState()
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showCropper by remember { mutableStateOf(false) }
    var showCameraScreen by remember { mutableStateOf(true) }
    var showChatScreen by remember { mutableStateOf(false) }
    var detectedText by remember { mutableStateOf("") }

    when {
        showCameraScreen -> {
            CameraWithOverlayScreen { bitmap ->
                capturedBitmap = bitmap
                showCropper = true
                showCameraScreen = false
            }
        }
        showCropper && capturedBitmap != null -> {
            CropImageScreen(
                originalBitmap = capturedBitmap!!,
                onCropConfirmed = { croppedBitmap ->
                    if (croppedBitmap != capturedBitmap) {
                        viewModel.processImage(croppedBitmap)
                    }
                    showCropper = false
                    capturedBitmap = null
                }
            )
        }
    }

    // Modificamos el manejo del texto detectado
    if (scanState is ScanState.Success) {
        detectedText = (scanState as ScanState.Success).text
        showChatScreen = true
        viewModel.reset()
    }

    if (showChatScreen) {
        ChatScreen(
            initialProblem = detectedText,
            onClose = {
                showChatScreen = false
                showCameraScreen = true
            }
        )
    }
}

@Composable
fun CameraWithOverlayScreen(
    onImageCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val density = LocalDensity.current.density
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    // Estado para el rectángulo guía
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

        if (previewSize != Size.Zero) {
            // Canvas para el overlay y el rectángulo
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
                val rectWidthPx = rectWidth.toPx()
                val rectHeightPx = rectHeight.toPx()

                // Área del rectángulo (transparente)
                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(
                        centerX - rectWidthPx / 2f,
                        centerY - rectHeightPx / 2f
                    ),
                    size = Size(rectWidthPx, rectHeightPx),
                    blendMode = androidx.compose.ui.graphics.BlendMode.Clear
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

            // Rectángulo redimensionable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(rectOffset.x.dp, rectOffset.y.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(rectWidth, rectHeight)
                        .align(Alignment.Center)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                rectOffset = Offset(
                                    rectOffset.x + dragAmount.x / density,
                                    rectOffset.y + dragAmount.y / density
                                )
                            }
                        }
                ) {
                    // Esquina superior izquierda
                    Box(modifier = Modifier.align(Alignment.TopStart)) {
                        ResizeCorner(
                            onDrag = { dragAmount ->
                                rectWidth = (rectWidth.value - dragAmount.x / density).coerceIn(100f, 300f).dp
                                rectHeight = (rectHeight.value - dragAmount.y / density).coerceIn(80f, 250f).dp
                            }
                        )
                    }

                    // Esquina superior derecha
                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                        ResizeCorner(
                            onDrag = { dragAmount ->
                                rectWidth = (rectWidth.value + dragAmount.x / density).coerceIn(100f, 300f).dp
                                rectHeight = (rectHeight.value - dragAmount.y / density).coerceIn(80f, 250f).dp
                            }
                        )
                    }

                    // Esquina inferior izquierda
                    Box(modifier = Modifier.align(Alignment.BottomStart)) {
                        ResizeCorner(
                            onDrag = { dragAmount ->
                                rectWidth = (rectWidth.value - dragAmount.x / density).coerceIn(100f, 300f).dp
                                rectHeight = (rectHeight.value + dragAmount.y / density).coerceIn(80f, 250f).dp
                            }
                        )
                    }

                    // Esquina inferior derecha
                    Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                        ResizeCorner(
                            onDrag = { dragAmount ->
                                rectWidth = (rectWidth.value + dragAmount.x / density).coerceIn(100f, 300f).dp
                                rectHeight = (rectHeight.value + dragAmount.y / density).coerceIn(80f, 250f).dp
                            }
                        )
                    }
                }
            }
        }

        // Botón de captura
        Button(
            onClick = {
                isCapturing = true
                val outputOptions = ImageCapture.OutputFileOptions.Builder(
                    File.createTempFile("image", ".jpg", context.cacheDir)
                ).build()

                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exception: ImageCaptureException) {
                            Log.e("Camera", "Capture failed: ${exception.message}")
                            isCapturing = false
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val file = File(output.savedUri?.path ?: return)
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            val correctedBitmap = correctImageOrientation(bitmap, file.absolutePath)
                            val croppedBitmap = cropBitmapToGuideRect(
                                correctedBitmap,
                                rectWidth,
                                rectHeight,
                                rectOffset,
                                previewSize
                            )
                            onImageCaptured(croppedBitmap)
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enabled = !isCapturing
        ) {
            Text(if (isCapturing) "Capturando..." else "Capturar")
        }

        if (isCapturing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
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



// Primero creamos un modelo para los mensajes
data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// 3. Actualiza el ChatScreen para incluir la funcionalidad de Gemini
@Composable
fun ChatScreen(
    initialProblem: String,
    onClose: () -> Unit
) {
    var messages by remember { mutableStateOf(listOf(ChatMessage(initialProblem, true))) }
    var isWaitingResponse by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }
    val geminiService = remember { GeminiService() }
    val scope = rememberCoroutineScope()

    // Procesar el problema inicial automáticamente
    LaunchedEffect(initialProblem) {
        if (messages.size == 1 && messages.first().isFromUser) {
            isWaitingResponse = true
            try {
                val response = geminiService.solveProblem(initialProblem)
                messages = messages + ChatMessage(response, false)
            } catch (e: Exception) {
                messages = messages + ChatMessage("Error al procesar el problema: ${e.message}", false)
            }
            isWaitingResponse = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Barra superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Resolución de Problemas",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        // Lista de mensajes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            reverseLayout = true
        ) {
            items(messages.asReversed()) { message ->
                ChatMessageBubble(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Indicador de escritura
        if (isWaitingResponse) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Procesando...")
            }
        }

        // Campo de entrada para nuevas preguntas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Haz una pregunta sobre el problema...") },
                enabled = !isWaitingResponse,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (userInput.isNotBlank()) {
                        val newUserMessage = ChatMessage(userInput, true)
                        messages = messages + newUserMessage

                        scope.launch {
                            isWaitingResponse = true
                            try {
                                val response = geminiService.continueChatConversation(
                                    messages.dropLast(1), // Excluir el último mensaje que acabamos de agregar
                                    userInput
                                )
                                messages = messages + ChatMessage(response, false)
                            } catch (e: Exception) {
                                messages = messages + ChatMessage("Error: ${e.message}", false)
                            }
                            isWaitingResponse = false
                        }

                        userInput = ""
                    }
                },
                enabled = !isWaitingResponse && userInput.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar")
            }
        }

        // Botón para escanear nuevo problema
        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Call, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Escanear nuevo problema")
        }
    }
}

// 4. Actualiza el ChatMessageBubble para mejor presentación
@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromUser) {
            Icon(
                Icons.Default.Star,
                contentDescription = "AI",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
                    .align(Alignment.Top),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (message.isFromUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (message.isFromUser) 12.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 12.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (message.isFromUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (message.isFromUser) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Usuario",
                modifier = Modifier
                    .size(32.dp)
                    .padding(start = 8.dp)
                    .align(Alignment.Top),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}