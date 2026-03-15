package domain.usecases

import domain.model.ChatBotMessage
import domain.repositories.ChatBotRepository

class ContinueChatUseCase(
    private val repository: ChatBotRepository
) {
    suspend operator fun invoke(messages: List<ChatBotMessage>, newMessage: String): String {
        return repository.continueChat(messages, newMessage)
    }
}

