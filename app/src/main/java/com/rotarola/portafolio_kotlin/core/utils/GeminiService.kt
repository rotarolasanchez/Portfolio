package com.rotarola.portafolio_kotlin.core.utils

import com.google.ai.client.generativeai.GenerativeModel
import com.rotarola.portafolio_kotlin.presentation.view.pages.ChatMessage

// 2. Crea un servicio para manejar la API de Gemini
class GeminiService {
    companion object {
        private const val API_KEY = "AIzaSyB2CzthJe8GvQWENALumNJe_QkQYOaj7FM" // Reemplaza con tu API key
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = API_KEY
    )

    suspend fun solveProblem(problemText: String): String {
        return try {
            val prompt = """
            Eres un asistente educativo experto en resolver problemas matemáticos y de ciencias.
            
            Problema detectado: $problemText
            
            Por favor:
            1. Analiza el problema paso a paso
            2. Proporciona la solución detallada
            3. Explica cada paso de manera clara
            4. Si hay conceptos importantes, explícalos brevemente
            
            Responde de manera educativa y fácil de entender.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: "No se pudo generar una respuesta"
        } catch (e: Exception) {
            "Error al procesar el problema: ${e.message}"
        }
    }

    suspend fun continueChatConversation(
        conversationHistory: List<ChatMessage>,
        userMessage: String
    ): String {
        return try {
            val historyText = conversationHistory.joinToString("\n") { message ->
                if (message.isFromUser) "Usuario: ${message.text}" else "Asistente: ${message.text}"
            }

            val prompt = """
            Historial de conversación:
            $historyText
            
            Nueva pregunta del usuario: $userMessage
            
            Responde de manera educativa y útil, manteniendo el contexto de la conversación anterior.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: "No se pudo generar una respuesta"
        } catch (e: Exception) {
            "Error al procesar la consulta: ${e.message}"
        }
    }
}
