package com.rotarola.portafolio_kotlin.domain.usecases

import com.rotarola.portafolio_kotlin.domain.model.RequestState
//import com.rotarola.portafolio_kotlin.domain.model.UserModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/*
@RunWith(JUnit4::class)
class LoginUseCaseTest {

    private val userRepository = mock(UserRepository::class.java)
    private val loginUseCase = LoginUseCase(userRepository)

    @Before
    fun setUp() {
        `when`(userRepository.geUsersApp("rotarola", "1234")).thenReturn(
            flow {
                emit(listOf(UserModel("1", "Ronald", "1234")))
            }
        )
    }

     @Test
    fun `when user is valid then return success`() {
         runBlocking {
             val value = loginUseCase.isUserValid("rotarola", "1234")
             assertEquals(value, true)
         }
     }

    @Test
    fun `when user is not valid then return error`() {
        runBlocking {
            val value = loginUseCase.isUserValid(null, "")
            assertEquals(value, false)
        }
    }

    @Test
    fun `when getUserAPP is valid then return Success`() = runBlocking {
        val flow = loginUseCase.geUsersApp("rotarola", "1234")
        flow.collect { requestState ->
            when (requestState) {
                is RequestState.Success -> {
                    assertEquals(requestState.data.isNotEmpty(), true)
                }
                is RequestState.Error -> {
                    fail("Expected success but got error: ${requestState.error.message}")
                }
                else -> {
                    fail("Unexpected state: $requestState")
                }
            }
        }
    }
}*/