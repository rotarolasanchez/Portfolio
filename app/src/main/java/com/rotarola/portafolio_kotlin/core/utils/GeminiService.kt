package com.rotarola.portafolio_kotlin.core.utils

import com.google.ai.client.generativeai.GenerativeModel
import com.rotarola.portafolio_kotlin.BuildConfig
import com.rotarola.portafolio_kotlin.domain.model.ChatMessage

// 2. Crea un servicio para manejar la API de Gemini
class GeminiService {
    private val requestCache = mutableMapOf<String, Pair<String, Long>>()
    private val CACHE_DURATION = 5 * 60 * 1000L
    private var lastRequestTime = 0L
    private val MIN_REQUEST_INTERVAL = 1000L

    private val generativeModel = GenerativeModel(
        modelName = BuildConfig.MODEL_NAME,
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private companion object {
        const val MAX_HISTORY_SIZE = 10
        const val MAX_MESSAGE_LENGTH = 2000
        const val MAX_INPUT_LENGTH = 5000
    }

    suspend fun solveProblem(problemText: String): String {
        return try {
            // Rate limiting
            if (!checkRateLimit()) {
                return "Por favor espera un momento antes de hacer otra consulta"
            }

            // Verificar caché
            val cached = getCachedResponse(problemText)
            if (cached != null) return cached

            // Validar entrada
            if (problemText.isBlank() || problemText.length > MAX_INPUT_LENGTH) {
                return "Entrada inválida"
            }

            val sanitizedInput = sanitizeInput(problemText)
            val response = generativeModel.generateContent(getPrompt(sanitizedInput))
            val result = response.text ?: "No se pudo generar una respuesta"

            // Guardar en caché
            cacheResponse(problemText, result)

            result
        } catch (e: Exception) {
            logError(e)
            "Error al procesar el problema"
        }
    }

    suspend fun continueChatConversation(
        conversationHistory: List<ChatMessage>,
        userMessage: String
    ): String {
        return try {
            // Rate limiting
            if (!checkRateLimit()) {
                return "Por favor espera un momento antes de hacer otra consulta"
            }

            // Validar mensaje
            if (userMessage.length > MAX_MESSAGE_LENGTH) {
                return "Mensaje demasiado largo"
            }

            // Filtrar información sensible
            val sanitizedMessage = filterSensitiveData(userMessage)

            // Limitar historial
            val limitedHistory = conversationHistory.takeLast(MAX_HISTORY_SIZE)
            val historyText = limitedHistory.joinToString("\n") { message ->
                if (message.isFromUser) "Usuario: ${message.text}"
                else "Asistente: ${message.text}"
            }

            val prompt = """
            Historial de conversación:
            $historyText

            Nueva pregunta del usuario: $sanitizedMessage

            Responde de manera educativa y útil, manteniendo el contexto de la conversación anterior.
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: "No se pudo generar una respuesta"
        } catch (e: Exception) {
            logError(e)
            "Error al procesar la consulta"
        }
    }

    private fun checkRateLimit(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRequestTime < MIN_REQUEST_INTERVAL) {
            return false
        }
        lastRequestTime = currentTime
        return true
    }

    private fun getCachedResponse(key: String): String? {
        val cached = requestCache[key] ?: return null
        val currentTime = System.currentTimeMillis()
        return if (currentTime - cached.second < CACHE_DURATION) {
            cached.first
        } else {
            requestCache.remove(key)
            null
        }
    }

    private fun cacheResponse(key: String, response: String) {
        requestCache[key] = Pair(response, System.currentTimeMillis())
    }

    private fun sanitizeInput(input: String): String {
        return input
            .trim()
            .replace(Regex("[<>\"'`]"), "")
            .take(MAX_INPUT_LENGTH)
    }

    private fun filterSensitiveData(text: String): String {
        return text
            .replace(Regex("\\b\\d{3}-\\d{2}-\\d{4}\\b"), "[DATO_PROTEGIDO]")
            .replace(Regex("\\b\\d{16}\\b"), "[DATO_PROTEGIDO]")
            .replace(Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"), "[EMAIL]")
    }

    private fun logError(e: Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    fun getPrompt(message: String): String {
        return message.trimIndent()
    }
}