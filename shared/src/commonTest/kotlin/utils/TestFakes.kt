package utils

import domain.model.ChatBotMessage
import domain.model.UserModel
import domain.repositories.AuthRepository
import domain.repositories.ChatBotRepository
import core.model.PlatformBitmap

// ─────────────────────────────────────────────
//  Fakes reutilizables para todos los tests
// ─────────────────────────────────────────────

/**
 * Fake de AuthRepository configurable para distintos escenarios de tests.
 */
class FakeAuthRepository(
    private val signInResult: Result<UserModel> = Result.success(
        UserModel(id = "1", email = "test@test.com", userName = "Test User")
    ),
    private val signUpResult: Result<UserModel> = Result.success(
        UserModel(id = "2", email = "new@test.com", userName = "New User")
    ),
    private val signOutResult: Result<Unit> = Result.success(Unit),
    private var currentUser: UserModel? = null
) : AuthRepository {

    var signInCallCount = 0
    var signUpCallCount = 0
    var signOutCallCount = 0
    var lastEmail: String? = null
    var lastPassword: String? = null

    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        signInCallCount++
        lastEmail = email
        lastPassword = password
        return signInResult
    }

    override suspend fun signInWithGoogle(): Result<UserModel> =
        Result.failure(NotImplementedError("Google Sign-In no implementado en tests"))

    override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> {
        signUpCallCount++
        return signUpResult
    }

    override suspend fun signOut(): Result<Unit> {
        signOutCallCount++
        return signOutResult
    }

    override fun getCurrentUser(): UserModel? = currentUser

    fun setCurrentUser(user: UserModel?) {
        currentUser = user
    }
}

/**
 * Fake de ChatBotRepository configurable para distintos escenarios de tests.
 */
class FakeChatBotRepository(
    private val analyzeImageResult: String = "Texto extraído de la imagen",
    private val solveProblemResult: String = "Solución del problema",
    private val continueChatResult: String = "Respuesta del chatbot",
    private val shouldThrow: Boolean = false,
    private val errorMessage: String = "Error del servidor"
) : ChatBotRepository {

    var analyzeImageCallCount = 0
    var solveProblemCallCount = 0
    var continueChatCallCount = 0
    var lastMessages: List<ChatBotMessage>? = null
    var lastNewMessage: String? = null
    var lastProblem: String? = null

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
        analyzeImageCallCount++
        if (shouldThrow) throw Exception(errorMessage)
        return analyzeImageResult
    }

    override suspend fun solveProblem(problem: String): String {
        solveProblemCallCount++
        lastProblem = problem
        if (shouldThrow) throw Exception(errorMessage)
        return solveProblemResult
    }

    override suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String {
        continueChatCallCount++
        lastMessages = messages
        lastNewMessage = newMessage
        if (shouldThrow) throw Exception(errorMessage)
        return continueChatResult
    }
}

