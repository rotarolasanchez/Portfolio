package com.rotarola.portafolio_kotlin.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.rotarola.portafolio_kotlin.data.datasources.AuthDataSource
import com.rotarola.portafolio_kotlin.data.entity.User
import com.rotarola.portafolio_kotlin.domain.model.UserModel
import com.rotarola.portafolio_kotlin.domain.repositories.AuthRepository
import com.rotarola.portafolio_kotlin.domain.usecases.SignInWithEmailUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.get
import kotlin.text.set
import kotlin.toString

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
                    username = firebaseUser.email ?: "",
                    password = ""
                )
                Log.e("AuthRepositoryImpl", "Login exitoso - UserModel: id=${userModel.id}, email=${userModel.username}")
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

    override suspend fun signInWithGoogle(): Result<User> {
        TODO("Implementar signInWithGoogle")
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        TODO("Implementar signUp")
    }

    override suspend fun signOut(): Result<Unit> {
        TODO("Implementar signOut")
    }

    override fun getCurrentUser(): User? {
        TODO("Implementar getCurrentUser")
    }
}
