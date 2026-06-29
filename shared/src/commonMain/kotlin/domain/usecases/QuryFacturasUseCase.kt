package domain.usecases

import domain.repositories.ChatBotRepository

class QueryFacturasUseCase(
    private val repository: ChatBotRepository
) {
    suspend operator fun invoke(question: String): String {
        return repository.queryFacturas(question)
    }
}