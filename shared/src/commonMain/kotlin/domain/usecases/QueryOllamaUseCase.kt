
package domain.usecases

import domain.repositories.ChatBotRepository

class QueryOllamaUseCase(
    private val repository: ChatBotRepository
) {
    suspend operator fun invoke(question: String): String {
        return repository.queryOllama(question)
    }
}
