package presentation.state

import domain.model.ChatBotMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChatBotUiStateTest {

    @Test
    fun `default state has empty values`() {
        val state = ChatBotUiState()

        assertTrue(state.messages.isEmpty())
        assertFalse(state.isProcessing)
        assertNull(state.error)
        assertFalse(state.showCamera)
        assertNull(state.capturedImageBytes)
        assertFalse(state.isLoading)
    }

    @Test
    fun `copy with new messages`() {
        val state = ChatBotUiState()
        val message = ChatBotMessage("Hola", isFromUser = true)

        val newState = state.copy(messages = state.messages + message)

        assertEquals(1, newState.messages.size)
        assertEquals("Hola", newState.messages[0].text)
        assertTrue(newState.messages[0].isFromUser)
    }

    @Test
    fun `copy preserves other fields`() {
        val state = ChatBotUiState(
            messages = listOf(ChatBotMessage("Test", isFromUser = true)),
            isProcessing = true,
            error = "Error de prueba"
        )

        val newState = state.copy(isProcessing = false)

        assertEquals(1, newState.messages.size)
        assertFalse(newState.isProcessing)
        assertEquals("Error de prueba", newState.error)
    }

    @Test
    fun `toCameraUiState without captured image returns Preview state`() {
        val chatState = ChatBotUiState()

        val cameraState = chatState.toCameraUiState()

        assertEquals(CameraState.Preview, cameraState.currentState)
        assertNull(cameraState.capturedImageBytes)
    }

    @Test
    fun `toCameraUiState with captured image returns ImagePreview state`() {
        val imageBytes = byteArrayOf(1, 2, 3)
        val chatState = ChatBotUiState(capturedImageBytes = imageBytes)

        val cameraState = chatState.toCameraUiState()

        assertEquals(CameraState.ImagePreview, cameraState.currentState)
        assertTrue(cameraState.capturedImageBytes.contentEquals(imageBytes))
    }

    @Test
    fun `equality with same data`() {
        val state1 = ChatBotUiState(
            messages = listOf(ChatBotMessage("Hola", isFromUser = true)),
            isProcessing = false
        )
        val state2 = ChatBotUiState(
            messages = listOf(ChatBotMessage("Hola", isFromUser = true)),
            isProcessing = false
        )

        assertEquals(state1, state2)
    }

    @Test
    fun `multiple messages accumulation`() {
        var state = ChatBotUiState()

        // Simular agregar mensajes
        state = state.copy(messages = state.messages + ChatBotMessage("Hola", isFromUser = true))
        state = state.copy(messages = state.messages + ChatBotMessage("¡Hola!", isFromUser = false))
        state = state.copy(messages = state.messages + ChatBotMessage("¿Cómo estás?", isFromUser = true))

        assertEquals(3, state.messages.size)
        assertTrue(state.messages[0].isFromUser)
        assertFalse(state.messages[1].isFromUser)
        assertTrue(state.messages[2].isFromUser)
    }
}

