package com.rotarola.portafolio_kotlin.presentation.viewmodels

import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
/*
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
}*/