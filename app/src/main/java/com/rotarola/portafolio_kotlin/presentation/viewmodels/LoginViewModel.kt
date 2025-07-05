package com.rotarola.portafolio_kotlin.presentation.viewmodels

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.domain.model.RequestState
import com.rotarola.portafolio_kotlin.data.entity.UserApp
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import com.rotarola.portafolio_kotlin.presentation.state.LoginUiState
import com.rotarola.portafolio_kotlin.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
//import com.rotarola.data.util.database.RealmDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val realmDBService: RealmDBService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUserCode(userCode: String) {
        _uiState.value = _uiState.value.copy(userCode = userCode)
    }

    fun updateUserPassword(password: String) {
        _uiState.value = _uiState.value.copy(userPassword = password)
    }

    fun updateIsSnackBarSuccessful(isSuccessful: Boolean) {
        _uiState.value = _uiState.value.copy(isSnackBarSuccessful = isSuccessful)
    }

    fun getUsersApp(code: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loginRequest = RequestState.Loading)

            loginUseCase.geUsersApp(code, password)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(loginRequest = RequestState.Error(e))
                }
                .collect { users ->
                    _uiState.value = _uiState.value.copy(loginRequest = users)
                }
        }
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _uiState.value.snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    fun insertUser(userApp: UserApp) {
        viewModelScope.launch(Dispatchers.IO) {
            realmDBService.insertUserAPP(userApp)
        }
    }
}