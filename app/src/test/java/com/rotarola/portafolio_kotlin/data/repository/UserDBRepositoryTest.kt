package com.rotarola.portafolio_kotlin.data.repository

import com.rotarola.portafolio_kotlin.data.entity.UserApp
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class UserDBRepositoryTest {
    private val datasource = mock(UserDBRepository::class.java)

    @Before
    fun setUp() {
        `when`(datasource.geUsersApp("rotarola", "1234")).thenReturn(
            listOf(UserApp())
        )
    }

    @Test
    fun `when geUsersApp is called then return list of UserApp`() = runBlocking {
        println("Test started: when geUsersApp is called then return list of UserApp")
        val users = datasource.geUsersApp("rotarola", "1234")
        runBlocking {
            delay(5000)
        }
        println("Users retrieved: $users")
        users.forEach { user ->
            println("User details: _id=${user._id}, code=${user.code}, name=${user.name}, password=${user.password}, ownerID=${user.ownerID}, date=${user.date}")
        }
        assertEquals(users.isNotEmpty(), true)
        println("Test completed: when geUsersApp is called then return list of UserApp")
    }
}