package domain.repositories

import domain.model.UserModel

interface AuthRepository {
    suspend fun signInWithGoogle(): Result<UserModel>
    suspend fun signInWithEmail(email: String, password: String): Result<UserModel>
    suspend fun signUp(email: String, password: String, name: String): Result<UserModel>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): UserModel?
}

