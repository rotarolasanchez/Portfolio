package com.example.feature_login.presentation.viewmodels

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.feature_login.domain.entities.User
import com.example.feature_login.domain.usecases.LoginUseCase
import com.rotarola.data.model.RequestState
import com.rotarola.data.model.UserApp
import com.rotarola.data.util.database.RealmDBService
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
    //private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val realmDBService = RealmDBService()

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
        Log.e("REOS", "LoginViewModel-setSnackbarHostState called")
        _snackbarHostState.value = snackbarHostState
    }

    /*class LoginViewModelFactory(
        private val loginUseCase: LoginUseCase
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(
                loginUseCase
            ) as T
        }
    }*/

    fun updateIsSnackBarSuccessful(isSnackBarSuccessful: Boolean) {
        Log.e("REOS", "LoginViewModel-updateIsSnackBarSuccessful called")
        _isSnackBackBarSucessful.value = isSnackBarSuccessful
    }


    fun updateUserLogin(user: String) {
        _userCode.value = user
    }

    fun updatePasswordLogin(password: String) {
        _userPassword.value = password
    }

    fun validateUserPassword(user: String, password: String){


    }

    fun insertUser( userApp: UserApp) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.e("REOS", "LoginViewModel-initRealm called")
            realmDBService.insertUserAPP(userApp)
        }
    }
/*
    fun getUsersApp(code: String, password: String) {
        Log.e("REOS", "LoginViewModel-getUsersApp called")
        viewModelScope.launch {
            _usersRequest.value = RequestState.Loading
            loginUseCase.geUsersApp(code, password)
                .catch { e ->
                    _usersRequest.value = RequestState.Error(e)
                    Log.e("REOS", "LoginViewModel-getUsersApp error: $e")
                }
                .collect { users ->
                    _usersRequest.value =  users // users
                    Log.e("REOS", "LoginViewModel-getUsersApp users: $users")
                }
        }
    }*/
}