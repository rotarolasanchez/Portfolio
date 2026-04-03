package di

import core.service.GeminiCloudService
import core.service.TextRecognitionService
import domain.model.ChatBotMessage
import domain.repositories.ChatBotRepository
import core.model.PlatformBitmap

/**
 * Implementación del ChatBotRepository para Web/WASM
 */
class WebChatBotRepositoryImpl(
    private val textAnalyzer: TextRecognitionService,
    private val geminiCloudService: GeminiCloudService
) : ChatBotRepository {

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
        // Enviar la imagen directamente al servicio de Gemini para análisis multimodal
        return geminiCloudService.analyzeImage(bitmap)
    }

    override suspend fun solveProblem(problem: String): String {
        return geminiCloudService.solveProblem(problem)
    }

    override suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String {
        return geminiCloudService.continueChat(messages, newMessage)
    }
}

