package com.rotarola.portafolio_kotlin.presentation.view.templates

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import com.rotarola.portafolio_kotlin.presentation.view.organisms.LoginContent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginTemplateTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var realmDBService: RealmDBService

    @Inject
    lateinit var loginUseCase: LoginUseCase

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        hiltRule.inject()
        loginViewModel = LoginViewModel(loginUseCase, realmDBService)
        println("Setup complete: loginViewModel initialized")
    }

    @Test
    fun testLoginContentDetail() {
        println("Starting testLoginContentDetail--")

        composeTestRule.setContent {
            LoginContent(
                loginViewModel = loginViewModel,
                userCode = "",
                userPassword = "",
                onLoginClick = { code, password ->
                    // Handle login click
                },
                onLoginSuccess = {

                }
            )
        }

        // Espera a que Compose esté listo
        composeTestRule.waitForIdle()

        // Imprime la jerarquía de nodos para depuración
        composeTestRule.onRoot().printToLog("TAG")
        //composeTestRule.onRoot().printToLog("TAG")

        composeTestRule.onNodeWithText("Usuario")
            .performTextInput("testUser")

        composeTestRule.onNodeWithTag("guestButton")

        composeTestRule.onNodeWithTag("versionButton")

        // Verifica que el texto ingresado se muestra correctamente
        composeTestRule.onNodeWithText("testUser")
            .assertIsDisplayed()

        // Simula la entrada de texto en el campo de contraseña usando el testTag
        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("testPassword")

        // Verifica que el texto ingresado se muestra correctamente en EditableText
        composeTestRule.onNodeWithTag("passwordField")
            .assert(hasText("testPassword", ignoreCase = false, substring = true))

        println("testLoginContentDetail completed")
    }
}