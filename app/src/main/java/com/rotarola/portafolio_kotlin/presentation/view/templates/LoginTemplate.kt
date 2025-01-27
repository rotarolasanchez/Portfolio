package com.rotarola.feature_login.presentation.view.templates

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rotarola.portafolio_kotlin.presentation.viewmodels.LoginViewModel
import kotlinx.coroutines.launch
import com.rotarola.portafolio_kotlin.data.model.RequestState
import com.rotarola.feature_ui.presentation.atoms.AnimatedLiquidBackground
import com.rotarola.feature_ui.presentation.atoms.EditextM3
import com.rotarola.feature_ui.presentation.atoms.ElevatedButtonM3
import com.rotarola.feature_ui.presentation.atoms.HeaderImage
import com.rotarola.feature_ui.presentation.atoms.SimpleText
import com.rotarola.portafolio_kotlin.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun LoginTemplate(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    Log.e("LoginTemplate" +
            "", "LoginTemplate_________" +
            "____")
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
        loginViewModel= loginViewModel,
        onLoginClick = { code, password ->
            loginViewModel.getUsersApp(code, password)
        },
        onUserCodeChange = { loginViewModel.updateUserLogin(it) },
        onPasswordChange = { loginViewModel.updatePasswordLogin(it) }
    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun HandleSnackBar(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val usersRequest = loginViewModel.usersRequest.collectAsState()
        LaunchedEffect(usersRequest.value) {
            when (usersRequest.value) {
                is RequestState.Success -> {
                    loginViewModel.updateIsSnackBarSuccessful(true)
                    val successfulSnackbarHostState = SnackbarHostState()
                    scope.launch {
                        successfulSnackbarHostState.showSnackbar(
                            message = "Acceso Autorizado",
                            duration = SnackbarDuration.Short
                        )
                    }
                    loginViewModel.setSnackbarHostState(successfulSnackbarHostState)
                    onLoginSuccess()
                }

                is RequestState.Error -> {
                    loginViewModel.updateIsSnackBarSuccessful(false)
                    val errorSnackbarHostState = SnackbarHostState()
                    scope.launch {
                        errorSnackbarHostState.showSnackbar(
                            message = (usersRequest.value as RequestState.Error).error.toString(),
                            duration = SnackbarDuration.Short
                        )
                    }
                    loginViewModel.setSnackbarHostState(errorSnackbarHostState)
                }

                is RequestState.Idle -> {
                }

                is RequestState.Loading -> {
                }
            }
        }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginContent(
    userCode: String,
    userPassword: String,
    isSnackBackBarSuccessful: Boolean,
    loginViewModel: LoginViewModel,
    onLoginClick: (String, String) -> Unit,
    onUserCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
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
                AnimatedLiquidBackground()
                LoginContentDetail(
                    userCode = userCode,
                    userPassword = userPassword,
                    onLoginClick = onLoginClick,
                    onUserCodeChange = onUserCodeChange,
                    onPasswordChange = onPasswordChange
                )
            }
        }
    )
}

@Composable
fun LoginContentDetail(
    userCode: String,
    userPassword: String,
    onLoginClick: (String, String) -> Unit,
    onUserCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        /*HeaderImage(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally),
            type = "Login"
        )*/
        Image(
            painter = painterResource(id = R.mipmap.capibara_family),
            contentDescription = "My image"
        )

        var isPasswordVisible by remember { mutableStateOf(false) }

        EditextM3(
            id = 0,
            status = true,
            value = userCode,
            placeholder = "",
            label = "Usuario",
            leadingiconResourceId = rememberVectorPainter(image = Icons.Filled.AccountCircle),
            keyboardType = KeyboardType.Text,
            trailingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),
            textDownEditext = "",
            trailingiconStatus = false,
            trailingiconEvent = {},
            countMaxCharacter = 20,
            resultEditText = onUserCodeChange
        )

        EditextM3(
            id = 0,
            status = true,
            value = userPassword,
            placeholder = "",
            label = "Contraseña",
            leadingiconResourceId = rememberVectorPainter(image = Icons.Filled.Lock),
            keyboardType = KeyboardType.Password,
            trailingiconResourceId = painterResource(id = R.drawable.baseline_visibility_24),
            textDownEditext = "",
            trailingiconStatus = false,
            trailingiconEvent = {},
            countMaxCharacter = 20,
            isPasswordField = true,
            isPasswordVisible = isPasswordVisible,
            onPasswordVisibilityChanged = { newVisibility -> isPasswordVisible = newVisibility },
            resultEditText = onPasswordChange
        )

        ElevatedButtonM3(
            enabled = true,
            title = "Ingresar",
            leadingiconResourceId = rememberVectorPainter(image = Icons.Default.Check),
            onClick = { onLoginClick(userCode, userPassword) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SimpleText("V.1.0.0")
        }
    }
}
