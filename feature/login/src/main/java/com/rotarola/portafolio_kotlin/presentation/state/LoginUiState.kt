package com.rotarola.portafolio_kotlin.presentation.state

import UserModel
import androidx.compose.material3.SnackbarHostState
import com.rotarola.portafolio_kotlin.domain.model.RequestState

data class LoginUiState(
    val userCode: String = "",
    val userPassword: String = "",
    val isSnackBarSuccessful: Boolean = false,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val loginRequest: RequestState<List<UserModel>> = RequestState.Idle,
)