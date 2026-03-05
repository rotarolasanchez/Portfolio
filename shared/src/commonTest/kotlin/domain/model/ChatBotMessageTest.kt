package domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChatBotMessageTest {

    @Test
    fun `ChatBotMessage from user`() {
        val message = ChatBotMessage(
            text = "Hola",
            isFromUser = true,
            timestamp = 12345L
        )
        assertEquals("Hola", message.text)
        assertTrue(message.isFromUser)
        assertEquals(12345L, message.timestamp)
    }

    @Test
    fun `ChatBotMessage from bot`() {
        val message = ChatBotMessage(
            text = "¡Hola! ¿En qué puedo ayudarte?",
            isFromUser = false
        )
        assertEquals("¡Hola! ¿En qué puedo ayudarte?", message.text)
        assertFalse(message.isFromUser)
    }

    @Test
    fun `ChatBotMessage default timestamp is 0`() {
        val message = ChatBotMessage(text = "test", isFromUser = true)
        assertEquals(0L, message.timestamp)
    }

    @Test
    fun `ChatBotMessage equality`() {
        val msg1 = ChatBotMessage("Hola", isFromUser = true, timestamp = 100L)
        val msg2 = ChatBotMessage("Hola", isFromUser = true, timestamp = 100L)
        assertEquals(msg1, msg2)
    }
}

