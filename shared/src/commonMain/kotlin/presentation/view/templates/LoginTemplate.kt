package presentation.view.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import domain.model.RequestState
import domain.model.UserModel
import presentation.view.organisms.LoginContent
import presentation.viewmodels.AuthViewModel

@Composable
fun LoginTemplate(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by authViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        if (uiState.snackbarMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = uiState.snackbarMessage,
                duration = SnackbarDuration.Long
            )
            authViewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (uiState.isAuthenticated)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    contentColor = Color.White
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LoginContent(
                userCode = uiState.userCode,
                userPassword = uiState.userPassword,
                onUserCodeChange = { authViewModel.updateUserCode(it) },
                onPasswordChange = { authViewModel.updateUserPassword(it) },
                onLoginClick = { code, password ->
                    authViewModel.signInWithEmail(code, password)
                },
                onGuestClick = onLoginSuccess,
                rememberCredentials = uiState.rememberCredentials,
                onRememberCredentialsChange = { authViewModel.updateRememberCredentials(it) }
            )
        }
    }

    HandleLoginState(
        usersRequest = uiState.loginRequest,
        onLoginSuccess = onLoginSuccess,
        onUpdateSnackbar = { state, message ->
            authViewModel.updateIsSnackBarSuccessful(state)
            authViewModel.showSnackbar(message)
        }
    )
}

@Composable
fun HandleLoginState(
    usersRequest: RequestState<List<UserModel>>,
    onLoginSuccess: () -> Unit,
    onUpdateSnackbar: (Boolean, String) -> Unit
) {
    LaunchedEffect(usersRequest) {
        when (usersRequest) {
            is RequestState.Success -> {
                onUpdateSnackbar(true, "Acceso Autorizado")
                kotlinx.coroutines.delay(2000)
                onLoginSuccess()
            }
            is RequestState.Error -> {
                val errorMessage = usersRequest.error.toString()
                kotlinx.coroutines.delay(2000)
                onUpdateSnackbar(false, errorMessage)
            }
            RequestState.Idle -> { /* No action needed */ }
            RequestState.Loading -> { /* No action needed */ }
        }
    }
}
