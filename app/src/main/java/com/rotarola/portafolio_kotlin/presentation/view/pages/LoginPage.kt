package com.rotarola.portafolio_kotlin.presentation.view.pages

import androidx.compose.runtime.Composable
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