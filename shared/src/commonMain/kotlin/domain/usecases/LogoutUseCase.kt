package domain.usecases

import domain.repositories.AuthRepository

/**
 * Caso de uso para cerrar sesión.
 *
 * TDD 🟢 GREEN — implementación mínima para pasar los tests.
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}

