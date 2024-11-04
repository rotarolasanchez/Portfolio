package com.rotarola.feature_ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.KeyboardType
import com.rotarola.feature_ui.presentation.atoms.EditextM3
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class TextTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /*@Test
    fun testEditTextDebounce() = runBlocking {
        val initialValue = "Initial"
        var capturedValue = "Initial"

        composeTestRule.setContent {
            EditextM3(
                value = initialValue,
                placeholder = "Enter text",
                label = "Label",
                resultEditText = { value ->
                    capturedValue = value
                },
                keyboardType = KeyboardType.Text,
                leadingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),
                status = true,
                trailingiconEvent = {},
                trailingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),

                )
        }

        // Simulamos la entrada de texto
        composeTestRule.onNodeWithTag("myEditText")
            .performTextInput("Hello World")

        // Esperamos un tiempo mayor al debounce (300ms) para asegurarnos de que el valor ha sido capturado
        composeTestRule.waitForIdle()

        // Aserción para verificar que el debounce ha capturado correctamente el texto
        assert(capturedValue == "Hello World")
    }*/


    /*@Composable
    @Test
    fun testPasswordVisibilityToggle() {
        val isPasswordVisible = remember { mutableStateOf(false) }

        composeTestRule.setContent {
            EditextM3(
                value = "password123",
                placeholder = "Enter password",
                label = "Password",
                isPasswordField = true,
                isPasswordVisible = isPasswordVisible.value,
                onPasswordVisibilityChanged = { newVisibility ->
                    isPasswordVisible.value = newVisibility
                },
                resultEditText = {},
                keyboardType = KeyboardType.Text,
                leadingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),
                status = true,
                trailingiconEvent = {},
                trailingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),
            )
        }

        // Verifica que el campo de texto está inicialmente oculto (contraseña no visible)
        composeTestRule.onNodeWithTag("myEditText")
            .assertIsDisplayed()

        // Simula el clic en el ícono de visibilidad
        composeTestRule.onNodeWithTag("myEditText").performClick()

        // Aserción: La visibilidad debe haberse alternado
        assert(isPasswordVisible.value)

        // Verificamos que el ícono de visibilidad está presente y se puede hacer clic
        composeTestRule.onNodeWithTag("myEditText").assertHasClickAction()
    }

    @Test
    fun testTrailingIconVisibilityToggle() {
        var passwordVisibility = false

        composeTestRule.setContent {
            EditextM3(
                value = "TestPassword123",
                placeholder = "Enter password",
                label = "Password",
                isPasswordField = true,
                isPasswordVisible = passwordVisibility,
                onPasswordVisibilityChanged = { newVisibility ->
                    passwordVisibility = newVisibility
                },
                resultEditText = {},
                keyboardType = KeyboardType.Text,
                leadingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),
                status = true,
                trailingiconEvent = {},
                trailingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),
            )
        }

        // Verifica que el ícono de visibilidad de la contraseña esté inicialmente oculto
        composeTestRule.onNodeWithTag("myEditText").assertIsDisplayed()

        // Simulamos el clic en el trailing icon (ícono de visibilidad)
        composeTestRule.onNodeWithTag("myEditText").performClick()

        // Verificamos que la visibilidad de la contraseña haya cambiado
        assert(passwordVisibility)
    }

    @Test
    fun testMaxCharacterLimit() {
        val maxCharLimit = 10
        composeTestRule.setContent {
            EditextM3(
                value = "",
                placeholder = "Placeholder",
                label = "Label",
                resultEditText = {},
                keyboardType = KeyboardType.Text,
                leadingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),
                status = true,
                trailingiconEvent = {},
                trailingiconResourceId = rememberVectorPainter(image = Icons.Filled.CheckCircle),
            )
        }

        // Simula la entrada de texto
        composeTestRule.onNodeWithTag("myEditText").performTextInput("This text exceeds the max limit")

        // Verifica que el campo de texto contiene como máximo 10 caracteres
        composeTestRule.onNodeWithTag("myEditText").assertTextEquals("This text ")
    }*/
}