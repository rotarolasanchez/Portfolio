package core.service

import domain.model.ChatBotMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import presentation.view.organisms.PlatformBitmap

/**
 * Implementación iOS de TextRecognitionService.
 * Stub funcional — para OCR real usar Vision framework con wrapper Swift.
 */
class TextRecognitionServiceImpl : TextRecognitionService {
    override suspend fun recognizeText(imageData: ByteArray): String {
        return "Texto extraído vía Vision (iOS) - pendiente de integración nativa"
    }
}

/**
 * Implementación iOS de GeminiCloudService.
 * Stub para iOS - implementación completa requiere NSURLSession con interop Swift
 * o integración con URLSession mediante wrapper Swift.
 * 
 * Para producción:
 * 1. Crear wrapper Swift que encapsule URLSession
 * 2. Exportarlo a Kotlin via @ObjC
 * 3. Llamarlo desde aquí
 */
class GeminiCloudServiceImpl : GeminiCloudService {

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
        return withContext(Dispatchers.Default) {
            try {
                "Análisis de imagen completado. " +
                "Se han identificado elementos visuales en la imagen. " +
                "Para OCR real, requiere integración con Vision framework."
            } catch (e: Exception) {
                "Error al analizar la imagen en iOS: ${e.message}"
            }
        }
    }

    override suspend fun solveProblem(problem: String): String {
        return withContext(Dispatchers.Default) {
            try {
                buildString {
                    append("Solución al problema:\n\n")
                    append(problem.take(200))
                    append("\n\n")
                    append("Pasos sugeridos:\n")
                    append("1. Analiza cuidadosamente el problema\n")
                    append("2. Identifica los componentes principales\n")
                    append("3. Aplica razonamiento paso a paso\n")
                    append("4. Verifica tu solución\n\n")
                    append("Nota: Para respuestas de Gemini real, configura:\n")
                    append("- Cloud Function con GEMINI_API_KEY\n")
                    append("- Autenticación Firebase en iOS")
                }
            } catch (e: Exception) {
                "Error al resolver el problema en iOS: ${e.message}"
            }
        }
    }

    override suspend fun continueChat(
        messages: List<ChatBotMessage>,
        newMessage: String
    ): String {
        return withContext(Dispatchers.Default) {
            try {
                val historyText = messages.joinToString("\n") { msg ->
                    if (msg.isFromUser) "Tú: ${msg.text}" else "Asistente: ${msg.text}"
                }
                
                buildString {
                    if (historyText.isNotEmpty()) {
                        append("Historial:\n")
                        append(historyText.take(300))
                        append("\n\n")
                    }
                    append("Tu pregunta: $newMessage\n\n")
                    append("Respuesta:\n")
                    append("Estoy procesando tu pregunta. Para respuestas completas de Gemini, ")
                    append("asegúrate de que tu Cloud Function esté correctamente configurada ")
                    append("con Firebase Auth y Gemini API Key.")
                }
            } catch (e: Exception) {
                "Error al procesar la consulta en iOS: ${e.message}"
            }
        }
    }
}
