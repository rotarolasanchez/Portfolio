package presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.storage.CredentialsStorage
import domain.model.RequestState
import domain.usecases.SignInWithEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import presentation.state.AuthUiState

// ViewModel compatible con KMP - inyectado via Koin
class AuthViewModel(
    private val signWithEmailUseCase: SignInWithEmailUseCase,
    private val credentialsStorage: CredentialsStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Cargar credenciales guardadas al iniciar
        val remember = credentialsStorage.isRememberEnabled()
        val saved = credentialsStorage.loadCredentials()
        if (remember && saved != null) {
            _uiState.value = _uiState.value.copy(
                userCode = saved.email,
                userPassword = saved.password,
                rememberCredentials = true
            )
        } else {
            _uiState.value = _uiState.value.copy(rememberCredentials = remember)
        }
    }

    fun updateUserCode(userCode: String) {
        _uiState.value = _uiState.value.copy(userCode = userCode)
    }

    fun updateUserPassword(userPassword: String) {
        _uiState.value = _uiState.value.copy(userPassword = userPassword)
    }

    fun updateRememberCredentials(remember: Boolean) {
        credentialsStorage.setRememberEnabled(remember)
        _uiState.value = _uiState.value.copy(rememberCredentials = remember)
    }

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
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = "",
                loginRequest = RequestState.Loading
            )

            val result = signWithEmailUseCase(email, password)
            result.fold(
                onSuccess = { userModel ->
                    // Guardar credenciales si el usuario lo solicitó
                    if (_uiState.value.rememberCredentials) {
                        credentialsStorage.saveCredentials(email, password)
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = userModel,
                        isAuthenticated = true,
                        loginRequest = RequestState.Success(listOf(userModel))
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message.toString(),
                        loginRequest = RequestState.Error(error)
                    )
                }
            )
        }
    }

}