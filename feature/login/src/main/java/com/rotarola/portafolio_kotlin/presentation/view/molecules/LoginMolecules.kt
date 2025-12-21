package com.rotarola.portafolio_kotlin.presentation.view.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rotarola.portafolio_kotlin.presentation.view.atoms.LoginButton
import com.rotarola.portafolio_kotlin.presentation.view.atoms.LoginTextField

@Composable
fun LoginForm(
    userCode: String,
    userPassword: String,
    onUserCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LoginTextField(
            value = userCode,
            onValueChange = onUserCodeChange,
            label = "Usuario",
            leadingIcon = Icons.Filled.AccountCircle,
            countMaxCharacter = 30
        )

        LoginTextField(
            value = userPassword,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true,
            isPasswordVisible = isPasswordVisible,
            onPasswordVisibilityChanged = { isPasswordVisible = it }
        )
    }
}

@Composable
fun LoginActions(
    onLoginClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier,
    ) {
        LoginButton(
            text = "Ingresar",
            onClick = onLoginClick
        )

        TextButton(
            onClick = onGuestClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Ingresar como invitado")
        }
    }
}