package presentation.view.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.capibara_family_not_background
import portafolio_kotlin.shared.generated.resources.baseline_account_circle_24
import portafolio_kotlin.shared.generated.resources.baseline_lock_24
import portafolio_kotlin.shared.generated.resources.outline_login_24
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
    modifier: Modifier = Modifier
) {
    ResponsiveContainer(
        modifier = modifier,
        maxWidth = 400.dp,
        enableScroll = true
    ) {
        // Logo responsivo
        Image(
            painter = painterResource(Res.drawable.capibara_family_not_background),
            contentDescription = "Logo",
            modifier = Modifier
                .size(120.dp)
                .aspectRatio(1f)
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

        Spacer(modifier = Modifier.height(24.dp))

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
    }
}
