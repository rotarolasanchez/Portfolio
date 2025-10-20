package com.rotarola.portafolio_kotlin.presentation.state

import androidx.compose.material3.SnackbarHostState
import com.rotarola.portafolio_kotlin.domain.model.RequestState
import com.rotarola.portafolio_kotlin.domain.model.UserModel

data class LoginUiState(
    val userCode: String = "",
    val userPassword: String = "",
    val isSnackBarSuccessful: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val loginRequest: RequestState<List<UserModel>> = RequestState.Idle,
)