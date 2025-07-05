package com.rotarola.feature_login.presentation.view.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rotarola.portafolio_kotlin.domain.model.RequestState
import com.rotarola.portafolio_kotlin.domain.model.User
import com.rotarola.portafolio_kotlin.presentation.viewmodels.LoginViewModel
import com.rotarola.portafolio_kotlin.presentation.view.organisms.LoginContent

@Composable
fun LoginTemplate(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by loginViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = uiState.snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (uiState.isSnackBarSuccessful)
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
                onUserCodeChange = { loginViewModel.updateUserCode(it) },
                onPasswordChange = { loginViewModel.updateUserPassword(it) },
                onLoginClick = { code, password ->
                    loginViewModel.getUsersApp(code, password)
                },
                onGuestClick = onLoginSuccess
            )
        }
    }

    HandleLoginState(
        usersRequest = uiState.loginRequest,
        onLoginSuccess = onLoginSuccess,
        onUpdateSnackbar = { state, message ->
            loginViewModel.updateIsSnackBarSuccessful(state)
            loginViewModel.showSnackbar(message)
        }
    )
}

@Composable
fun HandleLoginState(
    usersRequest: RequestState<List<User>>,
    onLoginSuccess: () -> Unit,
    onUpdateSnackbar: (Boolean, String) -> Unit
) {
    LaunchedEffect(usersRequest) {
        when (usersRequest) {
            is RequestState.Success -> {
                onUpdateSnackbar(true, "Acceso Autorizado")
                onLoginSuccess()
            }
            is RequestState.Error -> {
                val errorMessage = usersRequest.error.toString()
                onUpdateSnackbar(false, errorMessage)
            }
            RequestState.Idle -> { /* No action needed */ }
            RequestState.Loading -> { /* No action needed */ }
        }
    }
}



