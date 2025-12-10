package com.rotarola.portafolio_kotlin.domain.repositories

import com.rotarola.portafolio_kotlin.data.entity.UserEntity
import com.rotarola.portafolio_kotlin.domain.model.UserModel

interface AuthRepository {
    suspend fun signInWithGoogle(): Result<UserEntity>
    suspend fun signInWithEmail(email: String, password: String): Result<UserModel>
    suspend fun signUp(email: String, password: String, name: String): Result<UserEntity>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): UserEntity?
}