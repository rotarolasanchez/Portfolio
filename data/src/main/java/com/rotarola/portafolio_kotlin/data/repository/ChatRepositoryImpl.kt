package com.rotarola.portafolio_kotlin.data.repository

import android.graphics.Bitmap
import com.rotarola.portafolio_kotlin.core.service.GeminiCloudService
import com.rotarola.portafolio_kotlin.core.service.TextRecognitionService
import com.rotarola.portafolio_kotlin.domain.model.ChatMessage
import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val textAnalyzer: TextRecognitionService,
    private val geminiCloudService: GeminiCloudService // ✅ Cambiado
) : ChatRepository {

    override suspend fun analyzeImage(bitmap: Bitmap): String {
        return textAnalyzer.recognizeText(bitmap)
    }

    override suspend fun solveProblem(problem: String): String {
        return geminiCloudService.solveProblem(problem)
    }

    override suspend fun continueChat(messages: List<ChatMessage>, newMessage: String): String {
        return geminiCloudService.continueChatConversation(messages, newMessage)
    }
}