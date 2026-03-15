package data.repository

import domain.model.UserModel
import domain.repositories.AuthRepository

/**
 * Implementación iOS de AuthRepository.
 *
 * Estado actual: stub funcional para desarrollo con Xcode Simulator.
 *
 * Para producción con Firebase Auth real se requiere:
 * 1. Agregar pod 'Firebase/Auth' en iosApp/Podfile
 * 2. Inicializar Firebase en el @main de la app iOS (ver MainiOS.kt)
 * 3. Crear wrapper Swift que exponga FirebaseAuth a Kotlin vía @ObjCClass
 * 4. Reemplazar el stub de signInWithEmail con la llamada al wrapper.
 */
class IosAuthRepositoryImpl : AuthRepository {

    // Sesión en memoria (reemplazar con Keychain en producción)
    private var currentUser: UserModel? = null

    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        return try {
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

