package com.rotarola.portafolio_kotlin.data.repository

import android.graphics.Bitmap
import com.rotarola.portafolio_kotlin.core.utils.GeminiService
import com.rotarola.portafolio_kotlin.core.utils.TextRecognitionAnalyzer
import com.rotarola.portafolio_kotlin.domain.model.ChatMessage
import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val textAnalyzer: TextRecognitionAnalyzer,
    private val geminiService: GeminiService
) : ChatRepository { // Implementa INTERFAZ

    override suspend fun analyzeImage(bitmap: Bitmap): String {
        return textAnalyzer.recognizeText(bitmap)
    }

    override suspend fun solveProblem(problem: String): String {
        return geminiService.solveProblem(problem)
    }

    override suspend fun continueChat(messages: List<ChatMessage>, newMessage: String): String {
        return geminiService.continueChatConversation(messages, newMessage)
    }
}