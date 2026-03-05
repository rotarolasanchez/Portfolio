package presentation.viewmodels

import domain.model.ChatBotMessage
import domain.usecases.AnalyzeImageUseCase
import domain.usecases.ContinueChatUseCase
import domain.usecases.SolveProblemUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import presentation.state.ScanState
import utils.FakeChatBotRepository
import utils.TestCoroutineRule
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests unitarios de ChatBotViewModel.
 *
 * Buenas prácticas aplicadas:
 * - Dispatcher controlado con StandardTestDispatcher.
 * - FakeChatBotRepository configurable por escenario (éxito / error).
 * - Separación clara de responsabilidades por función del ViewModel.
 * - Verificación de estado intermedio (isLoading / isProcessing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatBotViewModelTest {

    private lateinit var fakeRepository: FakeChatBotRepository
    private lateinit var viewModel: ChatBotViewModel

    @BeforeTest
    fun setUp() {
        TestCoroutineRule.setup()
        buildViewModel()
    }

    @AfterTest
    fun tearDown() {
        TestCoroutineRule.tearDown()
    }

    private fun buildViewModel(
        analyzeImageResult: String = "Texto extraído",
        solveProblemResult: String = "Solución del problema",
        continueChatResult: String = "Respuesta del chatbot",
        shouldThrow: Boolean = false,
        errorMessage: String = "Error del servidor"
    ) {
        fakeRepository = FakeChatBotRepository(
            analyzeImageResult = analyzeImageResult,
            solveProblemResult = solveProblemResult,
            continueChatResult = continueChatResult,
            shouldThrow = shouldThrow,
            errorMessage = errorMessage
        )
        viewModel = ChatBotViewModel(
            analyzeImageUseCase = AnalyzeImageUseCase(fakeRepository),
            solveProblemUseCase = SolveProblemUseCase(fakeRepository),
            continueChatUseCase = ContinueChatUseCase(fakeRepository)
        )
    }

    // ─── Estado inicial ───────────────────────────────────────────────

    @Test
    fun `estado inicial es correcto`() {
        val state = viewModel.uiState.value

        assertTrue(state.messages.isEmpty())
        assertFalse(state.isProcessing)
        assertNull(state.error)
        assertFalse(state.showCamera)
        assertFalse(state.isLoading)
        assertIs<ScanState.Initial>(viewModel.scanState.value)
    }

    // ─── sendMessage - éxito ──────────────────────────────────────────

    @Test
    fun `sendMessage agrega mensaje del usuario y respuesta del bot`() = runTest {
        // Act
        viewModel.sendMessage("Hola ChatBot")
        advanceUntilIdle()

        // Assert
        val messages = viewModel.uiState.value.messages
        assertEquals(2, messages.size)
        assertEquals("Hola ChatBot", messages[0].text)
        assertTrue(messages[0].isFromUser)
        assertEquals("Respuesta del chatbot", messages[1].text)
        assertFalse(messages[1].isFromUser)
    }

    @Test
    fun `sendMessage limpia isLoading al completar`() = runTest {
        // Act - lanzar y esperar que complete
        viewModel.sendMessage("Mensaje de prueba")
        advanceUntilIdle()

        // Assert - al finalizar isLoading debe ser false
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `sendMessage con texto vacío no hace nada`() = runTest {
        // Act
        viewModel.sendMessage("")
        viewModel.sendMessage("   ")
        advanceUntilIdle()

        // Assert - no se enviaron mensajes
        assertTrue(viewModel.uiState.value.messages.isEmpty())
        assertEquals(0, fakeRepository.continueChatCallCount)
    }

    @Test
    fun `sendMessage pasa mensajes históricos al repositorio`() = runTest {
        // Arrange - primer mensaje
        viewModel.sendMessage("Primer mensaje")
        advanceUntilIdle()

        // Act - segundo mensaje
        viewModel.sendMessage("Segundo mensaje")
        advanceUntilIdle()

        // Assert
        assertNotNull(fakeRepository.lastMessages)
        assertEquals(2, fakeRepository.continueChatCallCount)
    }

    // ─── sendMessage - error ──────────────────────────────────────────

    @Test
    fun `sendMessage con error agrega mensaje de error y limpia isLoading`() = runTest {
        // Arrange
        buildViewModel(shouldThrow = true, errorMessage = "Error de red")

        // Act
        viewModel.sendMessage("Mensaje que falla")
        advanceUntilIdle()

        // Assert
        val messages = viewModel.uiState.value.messages
        assertEquals(2, messages.size) // mensaje usuario + mensaje de error del bot
        assertTrue(messages[1].text.contains("Error"))
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // ─── Cámara ───────────────────────────────────────────────────────

    @Test
    fun `showCamera actualiza showCamera a true`() {
        viewModel.showCamera()

        assertTrue(viewModel.uiState.value.showCamera)
    }

    @Test
    fun `hideCamera actualiza showCamera a false`() {
        viewModel.showCamera()
        viewModel.hideCamera()

        assertFalse(viewModel.uiState.value.showCamera)
    }

    // ─── clearError ───────────────────────────────────────────────────

    @Test
    fun `clearError limpia el mensaje de error`() = runTest {
        // Arrange - provocar un error
        buildViewModel(shouldThrow = true)
        viewModel.sendMessage("Mensaje")
        advanceUntilIdle()

        // Act
        viewModel.clearError()

        // Assert
        assertNull(viewModel.uiState.value.error)
    }

    // ─── reset (ScanState) ────────────────────────────────────────────

    @Test
    fun `reset devuelve scanState a Initial`() = runTest {
        // Act
        viewModel.reset()

        // Assert
        assertIs<ScanState.Initial>(viewModel.scanState.value)
    }

    // ─── Acumulación de mensajes ──────────────────────────────────────

    @Test
    fun `conversacion de multiples turnos acumula mensajes correctamente`() = runTest {
        viewModel.sendMessage("Hola")
        advanceUntilIdle()

        viewModel.sendMessage("¿Cómo funciona Kotlin?")
        advanceUntilIdle()

        viewModel.sendMessage("Gracias")
        advanceUntilIdle()

        // 3 turnos => 6 mensajes (usuario + bot por cada turno)
        assertEquals(6, viewModel.uiState.value.messages.size)
    }
}


