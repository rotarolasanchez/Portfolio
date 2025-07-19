package com.rotarola.portafolio_kotlin.presentation.view.atoms

import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rotarola.feature_ui.presentation.atoms.ButtonM3
import com.rotarola.feature_ui.presentation.atoms.EditextM3
import com.rotarola.feature_ui.presentation.atoms.TextM3
import com.rotarola.portafolio_kotlin.R

@Composable
fun ChatBotTextField(
    value: String,
    label: String,
) {
    TextM3(
        id = 0,
        status = true,
        text = value,
        placeholder = "",
        label = label,
        keyboardType = KeyboardType.Text,
        textDownEditext = "",
        trailingiconStatus = false,
        countMaxCharacter = 20,
        resultEditText = { },
        leadingiconColor = MaterialTheme.colorScheme.primary,
        trailingIconOnClick = {},
        trailingiconColor = MaterialTheme.colorScheme.primary,
        leadingiconStatus = false,
        trailingiconResourceId = painterResource(id = R.drawable.baseline_visibility_24),
    )
}

@Composable
fun ChatBotEditText(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: Painter,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityChanged: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    testTag: String = "login_text_field",
    trailingIcon:Painter = painterResource(id = R.drawable.baseline_visibility_24),
    trailingIconStatus: Boolean = false,
    leadingIconStatus: Boolean = false,
    leadingIconOnClick: (String) -> Unit = {},
    trailingIconOnClick: (String) -> Unit = {},
    countMaxCharacter: Int = 20,

) {
    EditextM3(
        id = 0,
        status = true,
        value = value,
        placeholder = "",
        label = label,
        leadingiconResourceId = leadingIcon,
        keyboardType = KeyboardType.Text,
        textDownEditext = "",
        trailingIconStatus = trailingIconStatus,
        countMaxCharacter = countMaxCharacter,
        resultEditText = onValueChange,
        leadingiconColor = MaterialTheme.colorScheme.primary,
        isPasswordField = isPassword,
        isPasswordVisible = isPasswordVisible,
        modifier = modifier,
        onPasswordVisibilityChanged = { newVisibility ->
            onPasswordVisibilityChanged?.invoke(newVisibility)
        },
        trailingiconColor = MaterialTheme.colorScheme.primary,
        leadingIconStatus = leadingIconStatus,
        trailingiconResourceId = trailingIcon,
        leadingIconOnClick = leadingIconOnClick,
        trailingIconOnClick = trailingIconOnClick,
    )
}



@Composable
fun ChatBotButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth(),
    leadingiconResourceId:Int  = R.drawable.outline_login_24,
) {
    ButtonM3(
        title = text,
        enabled = enabled,
        leadingiconResourceId = leadingiconResourceId,
        leadingIconStatus = true,
        onClick = onClick,
        modifier  = modifier
            .height(56.dp)
            .padding(10.dp,0.dp)
    )
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