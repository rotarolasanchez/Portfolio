package com.rotarola.portafolio_kotlin.presentation.view.templates

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rotarola.feature_login.presentation.view.templates.LoginContentDetail
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import com.rotarola.portafolio_kotlin.domain.usecases.LoginUseCase
import com.rotarola.portafolio_kotlin.presentation.viewmodels.LoginViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginTemplateTest2 {

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
        println("Starting testLoginContentDetail")

        composeTestRule.setContent {
            LoginContentDetail(
                loginViewModel = loginViewModel,
                userCode = "",
                userPassword = "",
                onLoginClick = { code, password ->
                    // Handle login click
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Usuario").performTextInput("testUser")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("testPassword")
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Assert
        composeTestRule.onNodeWithText("Usuario").assertTextEquals("testUser")
        composeTestRule.onNodeWithText("Contraseña").assertTextEquals("testPassword")

        println("testLoginContentDetail completed")
    }
}