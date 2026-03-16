package presentation.view.atoms


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rotarola.feature_ui.presentation.atoms.EditextM3
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun ChatBotEditText(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: DrawableResource,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityChanged: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    testTag: String = "chat_text_field",
    trailingIcon: DrawableResource ,
    trailingIconStatus: Boolean = false,
    leadingIconStatus: Boolean = false,
    leadingIconOnClick: () -> Unit = {},
    trailingIconOnClick: () -> Unit = {},
    countMaxCharacter: Int = 200,
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
        leadingIconOnClick = { _ -> leadingIconOnClick() },
        trailingIconOnClick = { _ -> trailingIconOnClick() },
    )
}

@Composable
fun ChatBotButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier.fillMaxWidth(),
    leadingiconResourceId: DrawableResource
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.extraLarge,   // MD3 FilledButton = extraLarge (28dp → fully rounded)
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
            .height(56.dp)
            .padding(horizontal = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(leadingiconResourceId),
                contentDescription = text,
                tint = if (enabled) MaterialTheme.colorScheme.onPrimary
                      else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurfaceVariant
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

// CameraPreviewWithCapture está definida en Camera.kt como expect/actual
