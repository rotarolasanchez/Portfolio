package domain.repositories

import core.model.PlatformBitmap
import domain.model.ChatBotMessage

interface ChatBotRepository {
    suspend fun analyzeImage(bitmap: PlatformBitmap): String
    suspend fun solveProblem(problem: String): String
    suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String
    suspend fun queryFacturas(question: String): String  // ← AGREGAR
    suspend fun queryOllama(question: String): String  // ← NUEVO
}

