package com.rotarola.portafolio_kotlin.domain.repositories

import com.rotarola.portafolio_kotlin.data.entity.UserApp
import com.rotarola.portafolio_kotlin.data.datasources.UserLocalDataSource
import com.rotarola.portafolio_kotlin.data.repository.UserRepositoryImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class UserRepositoryImplTest {

    private val datasource = mock(UserLocalDataSource::class.java)
    private val userRepository = UserRepositoryImpl(datasource)

    @Before
    fun setUp() {
        `when`(datasource.geUsersApp("code", "password")).thenReturn(
            listOf(UserApp())
        )
    }

    @Test
    fun `when geUsersApp is called then return list of users 2`() = runBlocking {
        val flow = userRepository.geUsersApp("code", "password")
        flow.collect { users ->
            assertEquals(users.isNotEmpty(), true)
        }
    }
}