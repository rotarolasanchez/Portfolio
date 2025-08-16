package com.rotarola.portafolio_kotlin.domain.usecases

import com.rotarola.portafolio_kotlin.domain.model.ChatMessage
import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import javax.inject.Inject

class ContinueChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(messages: List<ChatMessage>, newMessage: String): String {
        return chatRepository.continueChat(messages, newMessage)
    }
}