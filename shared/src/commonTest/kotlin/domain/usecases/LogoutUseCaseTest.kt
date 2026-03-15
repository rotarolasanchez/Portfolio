package domain.usecases

import domain.model.UserModel
import kotlinx.coroutines.test.runTest
import utils.FakeAuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * ╔══════════════════════════════════════════════════════════════╗
 *  CICLO TDD — LogoutUseCase
 *
 *  PASO 1 🔴 RED   → Este test se escribe ANTES que el use case.
 *                    Al compilar por primera vez FALLARÁ porque
 *                    LogoutUseCase aún no existe.
 *  PASO 2 🟢 GREEN → Se crea LogoutUseCase con la mínima
 *                    implementación para que el test pase.
 *  PASO 3 🔵 REFACTOR → Se limpia el código sin romper el test.
 * ╚══════════════════════════════════════════════════════════════╝
 */
class LogoutUseCaseTest {

    // ─── 🔴 Test 1: caso exitoso ──────────────────────────────────────
    @Test
    fun `logout exitoso retorna Result success`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository(
            signOutResult = Result.success(Unit)
        )
        val useCase = LogoutUseCase(fakeRepo)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isSuccess)
    }

    // ─── 🔴 Test 2: delega al repositorio ────────────────────────────
    @Test
    fun `logout llama al repositorio exactamente una vez`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository()
        val useCase = LogoutUseCase(fakeRepo)

        // Act
        useCase()

        // Assert
        assertEquals(1, fakeRepo.signOutCallCount)
    }

    // ─── 🔴 Test 3: fallo del repositorio ────────────────────────────
    @Test
    fun `logout fallido retorna Result failure con el mensaje correcto`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository(
            signOutResult = Result.failure(Exception("Error de sesión"))
        )
        val useCase = LogoutUseCase(fakeRepo)

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Error de sesión", result.exceptionOrNull()?.message)
    }

    // ─── 🔴 Test 4: no llama signIn ni signUp ────────────────────────
    @Test
    fun `logout solo llama signOut, no otras operaciones del repositorio`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository()
        val useCase = LogoutUseCase(fakeRepo)

        // Act
        useCase()

        // Assert - solo signOut fue llamado
        assertEquals(0, fakeRepo.signInCallCount)
        assertEquals(0, fakeRepo.signUpCallCount)
        assertEquals(1, fakeRepo.signOutCallCount)
    }
}

