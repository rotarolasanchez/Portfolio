package com.rotarola.portafolio_kotlin.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.Observer
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import com.rotarola.portafolio_kotlin.data.model.RequestState
import com.rotarola.portafolio_kotlin.domain.entities.User
import com.rotarola.portafolio_kotlin.domain.usecases.LoginUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @Mock
    private lateinit var loginUseCase: LoginUseCase

    @Mock
    private lateinit var realmDBService: RealmDBService

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        realmDBService = mock(RealmDBService::class.java)
        loginUseCase = mock(LoginUseCase::class.java)
        loginViewModel = LoginViewModel(loginUseCase, realmDBService)
    }

    @Test
    fun testUpdateUserLogin() = runTest {
        val user = "rotarola"
        loginViewModel.updateUserLogin(user)
        assertEquals(user, loginViewModel.userCode.value)
    }

    @Test
    fun testUpdatePasswordLogin() = runTest {
        val password = "testPassword"
        loginViewModel.updatePasswordLogin(password)
        assertEquals(password, loginViewModel.userPassword.value)
    }
}