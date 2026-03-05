package domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class UserModelTest {

    @Test
    fun `UserModel default values are empty strings`() {
        val user = UserModel()
        assertEquals("", user.id)
        assertEquals("", user.email)
        assertEquals("", user.userName)
        assertEquals("", user.userCode)
    }

    @Test
    fun `UserModel with values`() {
        val user = UserModel(
            id = "123",
            email = "test@example.com",
            userName = "John Doe",
            userCode = "JD001"
        )
        assertEquals("123", user.id)
        assertEquals("test@example.com", user.email)
        assertEquals("John Doe", user.userName)
        assertEquals("JD001", user.userCode)
    }

    @Test
    fun `UserModel copy works correctly`() {
        val user = UserModel(id = "1", email = "a@b.com")
        val copied = user.copy(email = "new@email.com")

        assertEquals("1", copied.id)
        assertEquals("new@email.com", copied.email)
    }

    @Test
    fun `UserModel equality`() {
        val user1 = UserModel(id = "1", email = "test@test.com")
        val user2 = UserModel(id = "1", email = "test@test.com")
        val user3 = UserModel(id = "2", email = "other@test.com")

        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
    }
}

