package com.rotarola.portafolio_kotlin.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.Observer
import com.rotarola.portafolio_kotlin.data.model.RequestState
import com.rotarola.portafolio_kotlin.domain.entities.User
import com.rotarola.portafolio_kotlin.domain.usecases.LoginUseCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
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

@RunWith(JUnit4::class)
class LoginViewModelTest {

   private val loginUseCase = mock(LoginUseCase::class.java)
   private lateinit var viewModel: LoginViewModel

   @Before
   fun setUp() {
       Dispatchers.setMain(Dispatchers.Unconfined)
       viewModel = LoginViewModel(loginUseCase)
   }

   @Test
   fun `when getUsersApp is successful then update usersRequest`() = runBlockingTest {
       val users = listOf(User("1", "Ronald", "1234"))
       `when`(loginUseCase.geUsersApp("rotarola", "1234")).thenReturn(flowOf(RequestState.Success(users)))

       viewModel.getUsersApp("rotarola", "1234")

       viewModel.usersRequest.collect { state ->
           assertEquals(state, RequestState.Success(users))
       }
   }

}

