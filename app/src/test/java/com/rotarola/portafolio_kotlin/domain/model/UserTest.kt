package com.rotarola.portafolio_kotlin.domain.model

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserTest {
    @Test
    fun `when user is created then fields are set correctly`() {
        val userModel = UserModel("1", "username", "password")
        assertEquals(userModel.id, "1")
        assertEquals(userModel.username, "username")
        assertEquals(userModel.password, "password")
    }
}