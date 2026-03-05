package data.repository

import data.datasources.AuthDataSource
import domain.model.UserModel
import domain.repositories.AuthRepository

class AuthRepositoryImpl(
    private val local: AuthDataSource,
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        return try {
            println("AuthRepositoryImpl: signInWithEmail: $email")
            val firebaseUser = local.signInWithEmail(email, password)

            if (firebaseUser != null) {
                val userModel = UserModel(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                )
                println("AuthRepositoryImpl: Login exitoso - UserModel: id=${userModel.id}, email=${userModel.email}")
                Result.success(userModel)
            } else {
                println("AuthRepositoryImpl: Login fallido - Usuario no encontrado")
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            println("AuthRepositoryImpl: Login error: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<UserModel> {
        TODO("Implementar signInWithGoogle")
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> {
        TODO("Implementar signUp")
    }

    override suspend fun signOut(): Result<Unit> {
        TODO("Implementar signOut")
    }

    override fun getCurrentUser(): UserModel? {
        TODO("Implementar getCurrentUser")
    }
}