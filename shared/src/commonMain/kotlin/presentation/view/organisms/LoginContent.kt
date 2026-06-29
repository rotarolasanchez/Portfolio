package presentation.view.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.capibara_family_not_background
import presentation.view.atoms.safeImagePainter
import portafolio_kotlin.shared.generated.resources.baseline_account_circle_24
import portafolio_kotlin.shared.generated.resources.baseline_lock_24
import portafolio_kotlin.shared.generated.resources.outline_login_24
import core.utils.AppInfo
import presentation.view.atoms.ChatBotButton
import presentation.view.atoms.ChatBotEditText
import presentation.view.atoms.ResponsiveContainer

@Composable
fun LoginContent(
    userCode: String,
    userPassword: String,
    onUserCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: (String, String) -> Unit,
    onGuestClick: () -> Unit,
    rememberCredentials: Boolean = false,
    onRememberCredentialsChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    ResponsiveContainer(
        modifier = modifier,
        maxWidth = 400.dp,
        enableScroll = true
    ) {
        // Logo responsivo
        Image(
            painter = safeImagePainter(
                Res.drawable.capibara_family_not_background,
                "drawable/capibara_family_not_background.png"
            ),
            contentDescription = "Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de usuario
        ChatBotEditText(
            value = userCode,
            onValueChange = onUserCodeChange,
            label = "Usuario",
            leadingIcon = Res.drawable.baseline_account_circle_24,
            trailingIcon = Res.drawable.baseline_account_circle_24,
            leadingIconStatus = true,
            trailingIconStatus = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de contraseña
        ChatBotEditText(
            value = userPassword,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            leadingIcon = Res.drawable.baseline_lock_24,
            trailingIcon = Res.drawable.baseline_lock_24,
            isPassword = true,
            leadingIconStatus = true,
            trailingIconStatus = false,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp)) // MD3: small = 8dp (checkbox agrupa con contraseña)

        // Checkbox "Recordar contraseña"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberCredentials,
                onCheckedChange = onRememberCredentialsChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Recordar contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de login
        ChatBotButton(
            text = "Iniciar Sesión",
            onClick = { onLoginClick(userCode, userPassword) },
            enabled = userCode.isNotBlank() && userPassword.isNotBlank(),
            leadingiconResourceId = Res.drawable.outline_login_24,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de invitado
        TextButton(onClick = onGuestClick) {
            Text(
                text = "Continuar como invitado",
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Número de versión
        Text(
            text = "v${AppInfo.versionName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
