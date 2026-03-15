package domain.usecases

import domain.model.UserModel
import domain.repositories.AuthRepository

class SignInWithEmailUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserModel> {
        return authRepository.signInWithEmail(email, password)
    }
}

