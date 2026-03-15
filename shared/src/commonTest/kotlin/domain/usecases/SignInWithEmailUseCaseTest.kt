package domain.usecases

import domain.model.UserModel
import domain.repositories.AuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignInWithEmailUseCaseTest {

    // Fake del repositorio para tests (no necesitas Mockito en KMP)
    private class FakeAuthRepository(
        private val signInResult: Result<UserModel> = Result.success(
            UserModel(id = "1", email = "test@test.com", userName = "Test User")
        )
    ) : AuthRepository {
        var signInCalled = false
        var lastEmail: String? = null
        var lastPassword: String? = null

        override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
            signInCalled = true
            lastEmail = email
            lastPassword = password
            return signInResult
        }

        override suspend fun signInWithGoogle(): Result<UserModel> =
            Result.failure(NotImplementedError())

        override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> =
            Result.failure(NotImplementedError())

        override suspend fun signOut(): Result<Unit> = Result.success(Unit)

        override fun getCurrentUser(): UserModel? = null
    }

    @Test
    fun `signIn with valid credentials returns success`() = runTest {
        // Arrange
        val expectedUser = UserModel(id = "1", email = "test@test.com", userName = "Test User")
        val fakeRepository = FakeAuthRepository(
            signInResult = Result.success(expectedUser)
        )
        val useCase = SignInWithEmailUseCase(fakeRepository)

        // Act
        val result = useCase("test@test.com", "password123")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        assertTrue(fakeRepository.signInCalled)
        assertEquals("test@test.com", fakeRepository.lastEmail)
        assertEquals("password123", fakeRepository.lastPassword)
    }

    @Test
    fun `signIn with invalid credentials returns failure`() = runTest {
        // Arrange
        val fakeRepository = FakeAuthRepository(
            signInResult = Result.failure(Exception("Credenciales inválidas"))
        )
        val useCase = SignInWithEmailUseCase(fakeRepository)

        // Act
        val result = useCase("wrong@test.com", "wrongpassword")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Credenciales inválidas", result.exceptionOrNull()?.message)
    }

    @Test
    fun `signIn passes correct email and password to repository`() = runTest {
        // Arrange
        val fakeRepository = FakeAuthRepository()
        val useCase = SignInWithEmailUseCase(fakeRepository)

        // Act
        useCase("user@example.com", "mySecurePass")

        // Assert
        assertEquals("user@example.com", fakeRepository.lastEmail)
        assertEquals("mySecurePass", fakeRepository.lastPassword)
    }
}

