package data.repository

import di.IosViewModelHolder
import domain.model.UserModel
import domain.repositories.AuthRepository

/**
 * Implementación iOS de AuthRepository.
 * Guarda email y password en IosViewModelHolder para que
 * GeminiCloudServiceImpl los use al llamar a askGeminiIos.
 * La autenticación real contra Firebase la hace el servidor.
 */
class IosAuthRepositoryImpl : AuthRepository {

    private var currentUser: UserModel? = null

    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        if (email.isBlank() || password.length < 6) {
            return Result.failure(Exception("Email o contraseña inválidos"))
        }
        // Guardar en memoria — el servidor validará las credenciales al enviar cada mensaje
        IosViewModelHolder.savedEmail    = email
        IosViewModelHolder.savedPassword = password

        val user = UserModel(
            id       = "ios-${email.hashCode()}",
            email    = email,
            userName = email.substringBefore("@"),
            userCode = email
        )
        currentUser = user
        println("[iOS] AuthRepository: credenciales guardadas para $email")
        return Result.success(user)
    }

    override suspend fun signInWithGoogle(): Result<UserModel> =
        Result.failure(Exception("Google Sign-In no disponible en iOS"))

    override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> =
        Result.failure(Exception("Registro no disponible en iOS"))

    override suspend fun signOut(): Result<Unit> {
        currentUser = null
        IosViewModelHolder.savedEmail    = null
        IosViewModelHolder.savedPassword = null
        IosViewModelHolder.firebaseIdToken = null
        println("[iOS] AuthRepository: sesión cerrada")
        return Result.success(Unit)
    }

    override fun getCurrentUser(): UserModel? = currentUser
}
