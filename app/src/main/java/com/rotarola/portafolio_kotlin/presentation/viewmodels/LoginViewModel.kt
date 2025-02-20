package com.rotarola.portafolio_kotlin.presentation.viewmodels

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.domain.entities.User
import com.rotarola.portafolio_kotlin.data.model.RequestState
import com.rotarola.portafolio_kotlin.data.model.UserApp
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import com.rotarola.portafolio_kotlin.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
//import com.rotarola.data.util.database.RealmDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val realmDBService: RealmDBService
) : ViewModel() {

    private val _userCode = MutableStateFlow("")
    val userCode: StateFlow<String> get() = _userCode

    private val _userPassword = MutableStateFlow("")
    val userPassword: StateFlow<String> get() = _userPassword

    private val _usersRequest = MutableStateFlow<RequestState<List<User>>>(RequestState.Idle)
    val usersRequest: StateFlow<RequestState<List<User>>> get() = _usersRequest

    private val _isSnackBackBarSucessful = MutableStateFlow(false)
    val isSnackBackBarSucessful: StateFlow<Boolean> get() = _isSnackBackBarSucessful

    private val _snackbarHostState = MutableStateFlow<SnackbarHostState>(SnackbarHostState())
    val snackbarHostState: StateFlow<SnackbarHostState> get() = _snackbarHostState

    fun setSnackbarHostState(snackbarHostState: SnackbarHostState) {
        _snackbarHostState.value = snackbarHostState
    }

    fun updateIsSnackBarSuccessful(isSnackBarSuccessful: Boolean) {
        _isSnackBackBarSucessful.value = isSnackBarSuccessful
    }

    fun updateUserLogin(user: String) {
        _userCode.value = user
    }

    fun updatePasswordLogin(password: String) {
        _userPassword.value = password
    }

    fun validateUserPassword(user: String, password: String) {
        // Implement validation logic
    }

    fun insertUser(userApp: UserApp) {
        viewModelScope.launch(Dispatchers.IO) {
            realmDBService.insertUserAPP(userApp)
        }
    }

    fun getUsersApp(code: String, password: String) {
        viewModelScope.launch {
            _usersRequest.value = RequestState.Loading
            loginUseCase.geUsersApp(code, password)
                .catch { e ->
                    _usersRequest.value = RequestState.Error(e)
                }
                .collect { users ->
                    _usersRequest.value = users
                }
        }
    }
}