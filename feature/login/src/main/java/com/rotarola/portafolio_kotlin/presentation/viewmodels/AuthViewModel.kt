package com.rotarola.portafolio_kotlin.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.domain.model.RequestState
import com.rotarola.portafolio_kotlin.domain.usecases.SignInWithEmailUseCase
import com.rotarola.portafolio_kotlin.presentation.state.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// presentation/viewmodels/AuthViewModel.kt
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signWithEmailUseCase: SignInWithEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateUserCode(userCode: String) {
        _uiState.value = _uiState.value.copy(userCode = userCode)
    }

    fun updateUserPassword(userPassword: String) {
        _uiState.value = _uiState.value.copy(userPassword = userPassword)
    }

    // Y estos métodos en tu AuthViewModel
    fun updateIsSnackBarSuccessful(isSuccessful: Boolean) {
        _uiState.value = _uiState.value.copy(isSnackBarSuccessful = isSuccessful)
    }

    fun showSnackbar(message: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = message)
    }

    fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = "")
    }

    fun signInWithEmail(email: String, password: String) {
        Log.e("AuthViewModel", "signInWithEmail called with email: $email")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = "",
                loginRequest = RequestState.Loading // ← Agrega esto
            )

            val result = signWithEmailUseCase(email, password)
            result.fold(
                onSuccess = { userModel ->
                    Log.e("AuthViewModel", "Login exitoso: ${userModel.email}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = userModel,
                        isAuthenticated = true,
                        loginRequest = RequestState.Success(listOf(userModel)) // ← ESTO ES CLAVE
                    )
                },
                onFailure = { error ->
                    Log.e("AuthViewModel", "Login fallido: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message.toString(),
                        loginRequest = RequestState.Error(error) // ← Agrega esto
                    )
                }
            )
        }
    }

}