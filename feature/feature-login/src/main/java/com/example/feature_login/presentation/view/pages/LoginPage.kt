package com.example.feature_login.presentation.view.pages

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feature_login.domain.repositories.UserRepositoryImpl
import com.example.feature_login.domain.usecases.LoginUseCase
import com.example.feature_login.presentation.viewmodels.LoginViewModel
import com.rotarola.data.repository.UserDBRepository
import com.rotarola.data.util.database.RealmDBService
import com.rotarola.feature_login.presentation.view.templates.LoginTemplate

@Composable
fun LoginPage(onLoginSuccess: () -> Unit) {
    /*val realmDBService = RealmDBService()
    val userDBRepository = UserDBRepository(realmDBService)
    val userRepository = UserRepositoryImpl(userDBRepository)
    val loginUseCase = LoginUseCase(userRepository)
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.LoginViewModelFactory(
            loginUseCase
        )
    )*/
    //val loginViewModel: LoginViewModel = hiltViewModel()


    LoginTemplate(
        //loginViewModel = loginViewModel,
        onLoginSuccess = onLoginSuccess
        )
}