        return try {
            // TODO: Llamar a FirebaseAuth.signOut() iOS SDK real
import di.IosViewModelHolder
            currentUser = null
            println("[iOS] AuthRepository: Sesión cerrada")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
 * Guarda email y password en IosViewModelHolder para que
 * GeminiCloudServiceImpl los use al llamar a askGeminiIos.
 * La autenticación real contra Firebase la hace el servidor.
                    userCode = email
                )
                currentUser = user
                Result.success(user)
            } else {
                Result.failure(Exception("Email o contraseña inválidos"))
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
 * Implementación iOS de AuthRepository.

    override suspend fun signInWithGoogle(): Result<UserModel> =
        Result.failure(Exception("Google Sign-In no disponible en iOS"))

    override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> =
        Result.failure(Exception("Registro no disponible en iOS"))
 *
 * Estado actual: stub funcional para desarrollo con Xcode Simulator.
        currentUser = null
        IosViewModelHolder.savedEmail    = null
        IosViewModelHolder.savedPassword = null
        IosViewModelHolder.firebaseIdToken = null
        println("[iOS] AuthRepository: sesión cerrada")
        return Result.success(Unit)

    // Sesión en memoria (reemplazar con Keychain en producción)
    private var currentUser: UserModel? = null

            // TODO: Reemplazar con FirebaseAuth iOS SDK real
            if (email.isNotBlank() && password.length >= 6) {
                val user = UserModel(
                    id = "ios-${email.hashCode()}",
                    email = email,
                    userName = email.substringBefore("@"),
                    userCode = email
                )
                currentUser = user
                println("[iOS] AuthRepository: Login exitoso - $email")
                Result.success(user)
            } else {
                Result.failure(Exception("Email o contraseña inválidos"))
            }
        } catch (e: Exception) {
            println("[iOS] AuthRepository: Error - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<UserModel> {
        // TODO: Integrar Google Sign-In para iOS
        return Result.failure(Exception("Google Sign-In no disponible aún en iOS"))
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> {
        return try {
            // TODO: Reemplazar con FirebaseAuth createUser iOS SDK real
            if (email.isNotBlank() && password.length >= 6 && name.isNotBlank()) {
                val user = UserModel(
                    id = "ios-new-${email.hashCode()}",
                    email = email,
                    userName = name,
                    userCode = email
                )
                currentUser = user
                Result.success(user)
            } else {
                Result.failure(Exception("Datos de registro inválidos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            // TODO: Llamar a FirebaseAuth.signOut() iOS SDK real
            currentUser = null
            println("[iOS] AuthRepository: Sesión cerrada")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): UserModel? = currentUser
}




