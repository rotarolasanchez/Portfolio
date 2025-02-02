package com.rotarola.portafolio_kotlin.presentation.view.templates

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rotarola.feature_login.presentation.view.templates.LoginTemplate
import com.rotarola.portafolio_kotlin.R
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import com.rotarola.portafolio_kotlin.domain.entities.User
import com.rotarola.portafolio_kotlin.domain.repositories.UserRepository
import com.rotarola.portafolio_kotlin.domain.usecases.LoginUseCase
import com.rotarola.portafolio_kotlin.presentation.viewmodels.LoginViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginTemplateTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var loginUseCase: LoginUseCase

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var realmDBService: RealmDBService

    @Before
    fun setUp() {
        hiltRule.inject()
        loginViewModel = LoginViewModel(loginUseCase,realmDBService)
    }

    class FakeUserRepository : UserRepository {
        override fun geUsersApp(code: String, password: String): Flow<List<User>> {
            return flow {
                emit(listOf(User("0", code, password)))
            }
        }
    }

    class TestViewModelFactory(
        private val loginViewModel: LoginViewModel
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return loginViewModel as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    @Test
    fun testUserRegistrationAndLogin() {
        // Establece el contenido al composable LoginTemplate con la fábrica de ViewModel personalizada
        composeTestRule.setContent {
            val factory = TestViewModelFactory(loginViewModel)
            val viewModel: LoginViewModel = viewModel(factory = factory)
            LoginTemplate(loginViewModel = viewModel, onLoginSuccess = {})
        }

        // Simula la entrada de texto para los campos de usuario y contraseña
        composeTestRule.onNodeWithText("Usuario").performTextInput("rotarola")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("testPassword")

        // Realiza una acción de clic en el botón de inicio de sesión
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Verifica el resultado
        val expectedUser = "rotarola"
        val actualUser = loginViewModel.userCode.value
        assertEquals(expectedUser, actualUser)
    }
}