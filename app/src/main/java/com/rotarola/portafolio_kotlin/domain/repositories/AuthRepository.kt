package com.rotarola.portafolio_kotlin.domain.repositories

import com.rotarola.portafolio_kotlin.data.entity.User
import com.rotarola.portafolio_kotlin.domain.model.UserModel

interface AuthRepository {
    suspend fun signInWithGoogle(): Result<User>
    suspend fun signInWithEmail(email: String, password: String): Result<UserModel>
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): User?
}