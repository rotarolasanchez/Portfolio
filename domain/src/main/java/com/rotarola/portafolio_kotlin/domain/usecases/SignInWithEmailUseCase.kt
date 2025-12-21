package com.rotarola.portafolio_kotlin.domain.usecases

import UserModel
import com.rotarola.portafolio_kotlin.domain.repositories.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository // Inyecta INTERFAZ
) {
    suspend operator fun invoke(email: String, password: String): Result<UserModel> {
        return authRepository.signInWithEmail(email, password)
    }
}