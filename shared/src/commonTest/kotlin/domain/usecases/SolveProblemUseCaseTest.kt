package domain.usecases

import domain.model.ChatBotMessage
import domain.repositories.ChatBotRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

// Fake local para tests de casos de uso (independiente de utils.FakeChatBotRepository)
internal class FakeChatBotRepository(
    private val solveProblemResult: String = "Solución del problema",
    private val continueChatResult: String = "Respuesta del chat",
    private val analyzeImageResult: String = "Texto analizado de imagen"
) : ChatBotRepository {
    var solveProblemCalled = false
    var lastProblem: String? = null
    var continueChatCalled = false
    var lastMessages: List<ChatBotMessage>? = null
    var lastNewMessage: String? = null

    override suspend fun analyzeImage(bitmap: presentation.view.organisms.PlatformBitmap): String {
        return analyzeImageResult
    }

    override suspend fun solveProblem(problem: String): String {
        solveProblemCalled = true
        lastProblem = problem
        return solveProblemResult
    }

    override suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String {
        continueChatCalled = true
        lastMessages = messages
        lastNewMessage = newMessage
        return continueChatResult
    }
}

class SolveProblemUseCaseTest {

    @Test
    fun `solveProblem returns expected solution`() = runTest {
        // Arrange
        val expectedSolution = "La respuesta es 42"
        val fakeRepository = FakeChatBotRepository(solveProblemResult = expectedSolution)
        val useCase = SolveProblemUseCase(fakeRepository)

        // Act
        val result = useCase("¿Cuál es el sentido de la vida?")

        // Assert
        assertEquals(expectedSolution, result)
        assertEquals("¿Cuál es el sentido de la vida?", fakeRepository.lastProblem)
    }

    @Test
    fun `solveProblem delegates to repository`() = runTest {
        // Arrange
        val fakeRepository = FakeChatBotRepository()
        val useCase = SolveProblemUseCase(fakeRepository)

        // Act
        useCase("Problema de matemáticas")

        // Assert
        assertEquals(true, fakeRepository.solveProblemCalled)
    }
}


