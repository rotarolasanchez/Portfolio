package com.rotarola.portafolio_kotlin

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.rotarola.feature_login.presentation.view.templates.LoginTemplate
import com.rotarola.portafolio_kotlin.presentation.view.pages.LoginPage
import org.junit.Rule
import org.junit.Test

class SimpleLoginTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testUserRegistrationAndLogin() {
        // Establece el contenido al composable SimpleLogin
        composeTestRule.setContent {
            LoginPage({})
        }

        // Simula la entrada de texto para los campos de usuario y contraseña
        composeTestRule.onNodeWithText("Usuario").performTextInput("rotarola")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("testPassword")

        // Realiza una acción de clic en el botón de inicio de sesión
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Verifica el resultado
        composeTestRule.onNodeWithText("Welcome, rotarola!").assertExists()
    }
}