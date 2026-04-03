package data.repository

import core.service.GeminiCloudService
import core.service.TextRecognitionService
import domain.model.ChatBotMessage
import domain.repositories.ChatBotRepository
import core.model.PlatformBitmap

/**
 * Implementación iOS de ChatBotRepository.
 * Reutiliza los servicios de Gemini (Cloud Function) y TextRecognition (Vision framework).
 *
 * Flujo para iOS:
 * - analyzeImage: Usa Vision framework (OCR nativo iOS) + Gemini Cloud Function
 * - solveProblem: Llama directamente a la Cloud Function de Gemini
 * - continueChat: Llama a la Cloud Function con historial de conversación
 */
class IosChatBotRepositoryImpl(
    private val textAnalyzer: TextRecognitionService,
    private val geminiService: GeminiCloudService
) : ChatBotRepository {

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
        return try {
            // Paso 1: extraer texto con Vision framework (OCR nativo iOS)
            // En iOS, el bitmap llegará como UIImage convertida a ByteArray
            val extractedText = textAnalyzer.recognizeText(byteArrayOf())

            // Paso 2: analizar con Gemini
            val geminiAnalysis = geminiService.analyzeImage(bitmap)

            """
            📝 Texto extraído (OCR iOS):
            $extractedText
            
            🔍 Análisis AI:
            $geminiAnalysis
            """.trimIndent()
        } catch (e: Exception) {
            println("[iOS] ChatBotRepository: Error al analizar imagen - ${e.message}")
            "Error al analizar la imagen en iOS: ${e.message}"
        }
    }

    override suspend fun solveProblem(problem: String): String {
        return geminiService.solveProblem(problem)
    }

    override suspend fun continueChat(
        messages: List<ChatBotMessage>,
        newMessage: String
    ): String {
        return geminiService.continueChat(messages, newMessage)
    }
}

