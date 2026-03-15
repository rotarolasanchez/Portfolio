package domain.usecases

import domain.model.UserModel
import kotlinx.coroutines.test.runTest
import utils.FakeAuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * TDD completo — SignUpUseCase
 *
 * 🔴 RED    → Test escrito primero, SignUpUseCase no existe todavía.
 * 🟢 GREEN  → Se crea SignUpUseCase mínimo.
 * 🔵 REFACTOR → Código limpio, tests como documentación viva.
 */
class SignUpUseCaseTest {

    @Test
    fun `registro exitoso retorna usuario con datos correctos`() = runTest {
        // Arrange
        val expectedUser = UserModel(id = "99", email = "nuevo@test.com", userName = "Nuevo Usuario")
        val fakeRepo = FakeAuthRepository(
            signUpResult = Result.success(expectedUser)
        )
        val useCase = SignUpUseCase(fakeRepo)

        // Act
        val result = useCase(
            email = "nuevo@test.com",
            password = "pass1234",
            name = "Nuevo Usuario"
        )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("nuevo@test.com", result.getOrNull()?.email)
        assertEquals("Nuevo Usuario", result.getOrNull()?.userName)
    }

    @Test
    fun `registro llama al repositorio exactamente una vez`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository()
        val useCase = SignUpUseCase(fakeRepo)

        // Act
        useCase("test@test.com", "pass123", "Test")

        // Assert
        assertEquals(1, fakeRepo.signUpCallCount)
    }

    @Test
    fun `registro fallido propaga el error del repositorio`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository(
            signUpResult = Result.failure(Exception("El correo ya está registrado"))
        )
        val useCase = SignUpUseCase(fakeRepo)

        // Act
        val result = useCase("duplicado@test.com", "pass123", "Usuario")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("El correo ya está registrado", result.exceptionOrNull()?.message)
    }

    @Test
    fun `registro con email vacío falla antes de llamar al repositorio`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository()
        val useCase = SignUpUseCase(fakeRepo)

        // Act
        val result = useCase(email = "", password = "pass123", name = "Usuario")

        // Assert — validación de entrada sin llamar al repo
        assertTrue(result.isFailure)
        assertEquals(0, fakeRepo.signUpCallCount)
        assertEquals("El email no puede estar vacío", result.exceptionOrNull()?.message)
    }

    @Test
    fun `registro con contraseña menor a 6 caracteres falla sin llamar al repositorio`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository()
        val useCase = SignUpUseCase(fakeRepo)

        // Act
        val result = useCase(email = "ok@test.com", password = "123", name = "Usuario")

        // Assert
        assertTrue(result.isFailure)
        assertEquals(0, fakeRepo.signUpCallCount)
        assertEquals("La contraseña debe tener al menos 6 caracteres", result.exceptionOrNull()?.message)
    }

    @Test
    fun `registro no realiza signIn ni signOut`() = runTest {
        // Arrange
        val fakeRepo = FakeAuthRepository()
        val useCase = SignUpUseCase(fakeRepo)

        // Act
        useCase("test@test.com", "pass1234", "Test")

        // Assert — solo signUp fue llamado
        assertEquals(0, fakeRepo.signInCallCount)
        assertEquals(0, fakeRepo.signOutCallCount)
        assertEquals(1, fakeRepo.signUpCallCount)
    }
}

