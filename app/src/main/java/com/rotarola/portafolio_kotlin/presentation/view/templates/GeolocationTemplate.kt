package com.rotarola.portafolio_kotlin.presentation.view.templates

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.rotarola.feature_login.presentation.view.templates.HandleSnackBar
import com.rotarola.feature_login.presentation.view.templates.LoginContent
import com.rotarola.feature_login.presentation.view.templates.LoginContentDetail
import com.rotarola.portafolio_kotlin.presentation.viewmodels.LoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun GeolocationTemplate(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {

    val userCode = loginViewModel.userCode.collectAsState()
    val userPassword = loginViewModel.userPassword.collectAsState()
    val isSnackBackBarSucessful = loginViewModel.isSnackBackBarSucessful.collectAsState()

    HandleSnackBar(
        loginViewModel = loginViewModel,
        onLoginSuccess = onLoginSuccess
    )

    LoginContent(
        userCode = userCode.value,
        userPassword = userPassword.value,
        isSnackBackBarSuccessful = isSnackBackBarSucessful.value,
        onLoginClick = { code, password ->
            loginViewModel.getUsersApp(code, password)
        },
        onLoginSuccess = onLoginSuccess
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginContent(
    userCode: String,
    userPassword: String,
    isSnackBackBarSuccessful: Boolean,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLoginClick: (String, String) -> Unit,
    onLoginSuccess: () -> Unit
) {
    val snackbarHostState = loginViewModel.snackbarHostState.collectAsState()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState.value) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isSnackBackBarSuccessful)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer,
                    contentColor = Color.White,
                    actionColor = Color.White
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                //AnimatedLiquidBackground()
                LoginContentDetail(
                    userCode = userCode,
                    userPassword = userPassword,
                    onLoginClick = onLoginClick,
                    onLoginSuccess = onLoginSuccess
                )
            }
        }
    )
}
