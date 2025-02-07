package com.rotarola.portafolio_kotlin.presentation.view.templates

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rotarola.feature_login.presentation.view.templates.LoginTemplate
import com.rotarola.portafolio_kotlin.MainActivity
import com.rotarola.portafolio_kotlin.R
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import com.rotarola.portafolio_kotlin.data.model.RequestState
import com.rotarola.portafolio_kotlin.data.model.UserApp
import com.rotarola.portafolio_kotlin.data.repository.UserDBRepository
import com.rotarola.portafolio_kotlin.di.RepositoryModule
import com.rotarola.portafolio_kotlin.domain.entities.User
import com.rotarola.portafolio_kotlin.domain.repositories.UserRepository
import com.rotarola.portafolio_kotlin.domain.repositories.UserRepositoryImpl
import com.rotarola.portafolio_kotlin.domain.usecases.LoginUseCase
import com.rotarola.portafolio_kotlin.presentation.viewmodels.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.realm.kotlin.Realm
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository {
        return LoginTemplateTest.FakeUserRepository()
    }
}

@UninstallModules(RepositoryModule::class)
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginTemplateTest {

    @get:Rule (order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule (order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var realmDBService: RealmDBService

    @Inject
    lateinit var loginUseCase: LoginUseCase

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        System.setProperty("IS_TEST_MODE", "true")
        hiltRule.inject() // Asegura que Hilt inyecta las dependencias
        loginViewModel = LoginViewModel(loginUseCase, realmDBService)
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
        composeTestRule.onNodeWithText("Usuario").performTextInput("dsadsdsadsadsadsads")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("1234")

        // Realiza una acción de clic en el botón de inicio de sesión
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Verifica el resultado
        val expectedUser = "dsadsdsadsadsadsads"
        val actualUser = loginViewModel.userCode.value
        assertEquals(expectedUser, actualUser)
    }


    @Test
    fun testPasswordRegistrationAndLogin() {
        // Establece el contenido al composable LoginTemplate con la fábrica de ViewModel personalizada
        composeTestRule.setContent {
            val factory = TestViewModelFactory(loginViewModel)
            val viewModel: LoginViewModel = viewModel(factory = factory)
            LoginTemplate(loginViewModel = viewModel, onLoginSuccess = {})
        }

        // Simula la entrada de texto para los campos de usuario y contraseña
        //composeTestRule.onNodeWithText("Usuario").performTextInput("rotarola")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("dsadsdsadsadsadsadsadadsadsa")
        // Simula la entrada de texto para los campos de usuario y contraseña
        //composeTestRule.onNodeWithTag("passwordField").performTextInput("rotarola")

        // Realiza una acción de clic en el botón de inicio de sesión
        //composeTestRule.onNodeWithText("Ingresar").performClick()

        // Verifica el resultado
        val expectedPassword = "dsadsdsadsadsadsadsadadsadsa"
        val actualPassword = loginViewModel.userPassword.value

        // Add logging
        Log.d("LoginTemplateTest", "actualPassword: $actualPassword")
        Log.d("LoginTemplateTest", "expectedPassword: $expectedPassword")

        assertEquals(expectedPassword, actualPassword)
    }

    @Test
    fun testLoginAndNavigateToMenu() {
        var navigatedToMenu = false

        // Establece el contenido al composable LoginTemplate con la fábrica de ViewModel personalizada
        composeTestRule.setContent {
            val factory = TestViewModelFactory(loginViewModel)
            val viewModel: LoginViewModel = viewModel(factory = factory)
            LoginTemplate(
                loginViewModel = viewModel,
                onLoginSuccess = { navigatedToMenu = true }
            )
        }

        // Simula la entrada de texto para los campos de usuario y contraseña
        composeTestRule.onNodeWithText("Usuario").performTextInput("rotarola")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("1234")

        // Realiza una acción de clic en el botón de inicio de sesión
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Verifica que se haya navegado al formulario de menú
        assertTrue(navigatedToMenu)
    }


}


