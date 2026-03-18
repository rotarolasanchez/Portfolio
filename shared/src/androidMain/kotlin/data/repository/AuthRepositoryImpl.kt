package data.repository

import com.google.firebase.auth.FirebaseAuth
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
        return Result.failure(UnsupportedOperationException("Sign in con Google aún no implementado"))
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> {
        return Result.failure(UnsupportedOperationException("Registro de usuario aún no implementado"))
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            FirebaseAuth.getInstance().signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): UserModel? {
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return null
        return UserModel(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: ""
        )
    }
}