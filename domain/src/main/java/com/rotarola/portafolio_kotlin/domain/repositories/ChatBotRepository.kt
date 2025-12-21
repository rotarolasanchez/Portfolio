package com.rotarola.portafolio_kotlin.domain.repositories

import android.graphics.Bitmap
import com.rotarola.portafolio_kotlin.domain.model.ChatBotMessage

interface ChatRepository {
    suspend fun analyzeImage(bitmap: Bitmap): String
    suspend fun solveProblem(problem: String): String
    suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String
}