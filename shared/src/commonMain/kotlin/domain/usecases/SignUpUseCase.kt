package domain.usecases

import domain.model.UserModel
import domain.repositories.AuthRepository

/**
 * Caso de uso para registrar un nuevo usuario.
 *
 * TDD 🟢 GREEN — implementación guiada por los tests:
 *   - Valida email no vacío
 *   - Valida contraseña mínimo 6 caracteres
 *   - Delega al repositorio solo si las validaciones pasan
 */
class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String
    ): Result<UserModel> {
        // 🔵 REFACTOR: validaciones de entrada antes de llamar al repo
        if (email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        return authRepository.signUp(email, password, name)
    }
}

