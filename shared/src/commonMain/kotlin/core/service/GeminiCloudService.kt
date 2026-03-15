package core.service

import domain.model.ChatBotMessage
import presentation.view.organisms.PlatformBitmap

/**
 * Servicio para interactuar con la API de Gemini para análisis de imágenes y chat
 */
interface GeminiCloudService {
    suspend fun analyzeImage(bitmap: PlatformBitmap): String
    suspend fun solveProblem(problem: String): String
    suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String
}

