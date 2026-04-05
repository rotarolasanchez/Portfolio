package presentation.viewmodels

import domain.model.RequestState
import domain.model.UserModel
import domain.usecases.LogoutUseCase
import domain.usecases.SignInWithEmailUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import utils.FakeAuthRepository
import utils.FakeCredentialsStorage
import utils.TestCoroutineRule
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Tests unitarios de AuthViewModel.
 *
 * Buenas prácticas aplicadas:
 * - Se usa StandardTestDispatcher para controlar la ejecución de corrutinas.
 * - Se usa FakeAuthRepository (sin Mockito) para compatibilidad KMP.
 * - Cada test sigue el patrón AAA (Arrange / Act / Assert).
 * - setUp y tearDown gestionan el ciclo de vida del dispatcher.
 * - Se verifica el estado ANTES y DESPUÉS de la operación asíncrona.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @BeforeTest
    fun setUp() {
        TestCoroutineRule.setup()
        fakeRepository = FakeAuthRepository()
        viewModel = AuthViewModel(
            signWithEmailUseCase = SignInWithEmailUseCase(fakeRepository),
            credentialsStorage = FakeCredentialsStorage(),
            logoutUseCase = LogoutUseCase(fakeRepository)
        )
    }

    @AfterTest
    fun tearDown() {
        TestCoroutineRule.tearDown()
    }

    // ─── updateUserCode ───────────────────────────────────────────────

    @Test
    fun `updateUserCode actualiza el estado correctamente`() {
        viewModel.updateUserCode("usuario@correo.com")

        assertEquals("usuario@correo.com", viewModel.uiState.value.userCode)
    }

    @Test
    fun `updateUserPassword actualiza el estado correctamente`() {
        viewModel.updateUserPassword("miPassword123")

        assertEquals("miPassword123", viewModel.uiState.value.userPassword)
    }

    // ─── signInWithEmail - éxito ──────────────────────────────────────

    @Test
    fun `signInWithEmail exitoso actualiza isAuthenticated a true`() = runTest {
        // Arrange
        val expectedUser = UserModel(id = "1", email = "test@test.com", userName = "Test User")
        fakeRepository = FakeAuthRepository(signInResult = Result.success(expectedUser))
        viewModel = AuthViewModel(
            SignInWithEmailUseCase(fakeRepository),
            FakeCredentialsStorage(),
            LogoutUseCase(fakeRepository)
        )

        // Act
        viewModel.signInWithEmail("test@test.com", "password123")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertFalse(state.isLoading)
        assertEquals("test@test.com", state.user.email)
        assertEquals("", state.error)
        assertIs<RequestState.Success<*>>(state.loginRequest)
    }

    @Test
    fun `signInWithEmail completa y limpia el estado Loading`() = runTest {
        // Arrange
        val expectedUser = UserModel(id = "1", email = "test@test.com", userName = "Test User")
        fakeRepository = FakeAuthRepository(signInResult = Result.success(expectedUser))
        viewModel = AuthViewModel(
            SignInWithEmailUseCase(fakeRepository),
            FakeCredentialsStorage(),
            LogoutUseCase(fakeRepository)
        )

        // Act - lanzar y esperar que complete
        viewModel.signInWithEmail("test@test.com", "password123")
        advanceUntilIdle()

        // Assert - al finalizar NO debe estar en Loading
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        // El estado final debe ser Success (no Loading)
        assertIs<RequestState.Success<*>>(state.loginRequest)
    }

    // ─── signInWithEmail - fallo ──────────────────────────────────────

    @Test
    fun `signInWithEmail fallido actualiza error correctamente`() = runTest {
        // Arrange
        fakeRepository = FakeAuthRepository(
            signInResult = Result.failure(Exception("Credenciales incorrectas"))
        )
        viewModel = AuthViewModel(
            SignInWithEmailUseCase(fakeRepository),
            FakeCredentialsStorage(),
            LogoutUseCase(fakeRepository)
        )

        // Act
        viewModel.signInWithEmail("wrong@test.com", "badpass")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertFalse(state.isAuthenticated)
        assertFalse(state.isLoading)
        assertEquals("Credenciales incorrectas", state.error)
        assertIs<RequestState.Error>(state.loginRequest)
    }

    @Test
    fun `signInWithEmail llama al repositorio con los datos correctos`() = runTest {
        // Act
        viewModel.signInWithEmail("usuario@correo.com", "pass123")
        advanceUntilIdle()

        // Assert
        assertEquals(1, fakeRepository.signInCallCount)
        assertEquals("usuario@correo.com", fakeRepository.lastEmail)
        assertEquals("pass123", fakeRepository.lastPassword)
    }

    // ─── Snackbar ─────────────────────────────────────────────────────

    @Test
    fun `showSnackbar actualiza snackbarMessage`() {
        viewModel.showSnackbar("Inicio de sesión exitoso")

        assertEquals("Inicio de sesión exitoso", viewModel.uiState.value.snackbarMessage)
    }

    @Test
    fun `clearSnackbarMessage limpia el mensaje`() {
        viewModel.showSnackbar("Mensaje temporal")
        viewModel.clearSnackbarMessage()

        assertEquals("", viewModel.uiState.value.snackbarMessage)
    }

    @Test
    fun `updateIsSnackBarSuccessful actualiza el flag`() {
        viewModel.updateIsSnackBarSuccessful(true)
        assertTrue(viewModel.uiState.value.isSnackBarSuccessful)

        viewModel.updateIsSnackBarSuccessful(false)
        assertFalse(viewModel.uiState.value.isSnackBarSuccessful)
    }

    // ─── Estado inicial ───────────────────────────────────────────────

    @Test
    fun `estado inicial del ViewModel es correcto`() {
        val state = viewModel.uiState.value

        assertEquals("", state.userCode)
        assertEquals("", state.userPassword)
        assertFalse(state.isAuthenticated)
        assertFalse(state.isLoading)
        assertEquals("", state.error)
        assertIs<RequestState.Idle>(state.loginRequest)
    }
}


