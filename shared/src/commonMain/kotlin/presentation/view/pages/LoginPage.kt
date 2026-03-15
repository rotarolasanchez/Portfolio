package presentation.view.pages

import androidx.compose.runtime.Composable
import presentation.view.templates.LoginTemplate
import presentation.viewmodels.AuthViewModel


@Composable
fun LoginPage(onLoginSuccess: () -> Unit) {
    val authViewModel = provideAuthViewModel()
    LoginTemplate(
        authViewModel = authViewModel,
        onLoginSuccess = onLoginSuccess
    )
}