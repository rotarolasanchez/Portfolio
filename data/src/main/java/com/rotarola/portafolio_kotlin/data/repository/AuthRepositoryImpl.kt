package com.rotarola.portafolio_kotlin.data.repository

import UserModel
import android.util.Log
import com.rotarola.portafolio_kotlin.data.datasources.AuthDataSource
import com.rotarola.portafolio_kotlin.data.entity.UserEntity
import com.rotarola.portafolio_kotlin.domain.repositories.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val local: AuthDataSource,
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        return try {
            Log.e("AuthRepositoryImpl", "signInWithEmail: $email")
            val firebaseUser = local.signInWithEmail(email, password)

            if (firebaseUser != null) {
                val userModel = UserModel(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                )
                Log.e("AuthRepositoryImpl", "Login exitoso - UserModel: id=${userModel.id}, email=${userModel.email}")
                Result.success(userModel)
            } else {
                Log.e("AuthRepositoryImpl", "Login fallido - Usuario no encontrado")
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Login error: ${e.message}", e)
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