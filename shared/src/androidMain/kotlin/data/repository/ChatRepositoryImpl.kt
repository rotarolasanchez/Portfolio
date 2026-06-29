package data.repository

import android.graphics.Bitmap
import core.service.GeminiCloudService
import core.service.TextRecognitionService
import domain.model.ChatBotMessage
import domain.repositories.ChatBotRepository
import core.model.PlatformBitmap
import java.io.ByteArrayOutputStream

class ChatBotRepositoryImpl(
    private val textAnalyzer: TextRecognitionService,
    private val geminiCloudService: GeminiCloudService
) : ChatBotRepository {

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
        val androidBitmap = bitmap as Bitmap

        // Primero extraer texto con OCR
        val byteArray = bitmapToByteArray(androidBitmap)
        val extractedText = textAnalyzer.recognizeText(byteArray)

        // Luego analizar con Gemini
        val geminiAnalysis = geminiCloudService.analyzeImage(androidBitmap)

        return """
        📝 Texto extraído:
        $extractedText
        
        🔍 Análisis AI:
        $geminiAnalysis
        """.trimIndent()
    }

    override suspend fun solveProblem(problem: String): String {
        return geminiCloudService.solveProblem(problem)
    }

    override suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String {
        return geminiCloudService.continueChat(messages, newMessage)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }
}