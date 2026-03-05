package domain.usecases

import domain.repositories.ChatBotRepository

class SolveProblemUseCase(
    private val repository: ChatBotRepository
) {
    suspend operator fun invoke(problem: String): String {
        return repository.solveProblem(problem)
    }
}

