package com.rotarola.portafolio_kotlin.presentation.view.pages

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.rotarola.feature_login.presentation.view.templates.LoginTemplate
import com.rotarola.portafolio_kotlin.presentation.viewmodels.LoginViewModel

@Composable
fun LoginPage(onLoginSuccess: () -> Unit) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    LoginTemplate(
        loginViewModel = loginViewModel,
        onLoginSuccess = onLoginSuccess
        )
}