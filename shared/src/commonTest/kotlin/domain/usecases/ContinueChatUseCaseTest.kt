package domain.usecases

import domain.model.ChatBotMessage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ContinueChatUseCaseTest {

    @Test
    fun `continueChat returns bot response`() = runTest {
        // Arrange
        val expectedResponse = "Hola, ¿en qué puedo ayudarte?"
        val fakeRepo = FakeChatBotRepository(continueChatResult = expectedResponse)
        val useCase = ContinueChatUseCase(fakeRepo)

        val messages = listOf(
            ChatBotMessage("Hola", isFromUser = true)
        )

        // Act
        val result = useCase(messages, "¿Cómo estás?")

        // Assert
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `continueChat passes messages and new message correctly`() = runTest {
        // Arrange
        val fakeRepo = FakeChatBotRepository()
        val useCase = ContinueChatUseCase(fakeRepo)

        val messages = listOf(
            ChatBotMessage("Hola", isFromUser = true),
            ChatBotMessage("¡Hola! ¿En qué puedo ayudarte?", isFromUser = false)
        )

        // Act
        useCase(messages, "Quiero saber sobre Kotlin")

        // Assert
        assertEquals(2, fakeRepo.lastMessages?.size)
        assertEquals("Quiero saber sobre Kotlin", fakeRepo.lastNewMessage)
    }

    @Test
    fun `continueChat with empty history works`() = runTest {
        // Arrange
        val fakeRepo = FakeChatBotRepository(continueChatResult = "Primera respuesta")
        val useCase = ContinueChatUseCase(fakeRepo)

        // Act
        val result = useCase(emptyList(), "Primer mensaje")

        // Assert
        assertEquals("Primera respuesta", result)
        assertTrue(fakeRepo.lastMessages!!.isEmpty())
    }
}


