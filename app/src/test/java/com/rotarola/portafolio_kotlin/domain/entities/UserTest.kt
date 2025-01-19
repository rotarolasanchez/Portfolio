package com.rotarola.portafolio_kotlin.domain.entities

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserTest {
    @Test
    fun `when user is created then fields are set correctly`() {
        val user = User("1", "username", "password")
        assertEquals(user.id, "1")
        assertEquals(user.username, "username")
        assertEquals(user.password, "password")
    }
}