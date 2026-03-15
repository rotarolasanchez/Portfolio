package domain.repositories

import domain.model.ChatBotMessage
import presentation.view.organisms.PlatformBitmap

interface ChatBotRepository {
    suspend fun analyzeImage(bitmap: PlatformBitmap): String
    suspend fun solveProblem(problem: String): String
    suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String
}

