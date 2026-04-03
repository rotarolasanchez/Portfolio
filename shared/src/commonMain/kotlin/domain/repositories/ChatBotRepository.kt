package domain.repositories

import core.model.PlatformBitmap
import domain.model.ChatBotMessage

interface ChatBotRepository {
    suspend fun analyzeImage(bitmap: PlatformBitmap): String
    suspend fun solveProblem(problem: String): String
    suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String
}

