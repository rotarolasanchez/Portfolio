package core.service

import core.interop.fetchImageWithAuth
import core.interop.fetchWithAuth
import core.interop.firebaseGetIdToken
import core.utils.Constans
import domain.model.ChatBotMessage
import core.model.PlatformBitmap

/**
 * Implementación de TextRecognitionService para Web
 */
class TextRecognitionServiceImpl : TextRecognitionService {
    override suspend fun recognizeText(imageData: ByteArray): String {
        return "Texto extraído desde imagen en navegador (OCR no disponible en web)"
    }
}

/**
 * Implementación de GeminiCloudService para Web
 * Usa Firebase Auth token para autenticarse con la Cloud Function
 * Soporta análisis de imágenes enviando base64 a la Cloud Function
 */
class GeminiCloudServiceImpl : GeminiCloudService {

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
        return try {
            val imageData = bitmap.imageData
            if (imageData != null && imageData.startsWith("data:image")) {
                val mimeType = imageData.substringAfter("data:").substringBefore(";base64,")
                val base64Data = imageData.substringAfter(";base64,")

                // Una sola llamada multimodal: Gemini hace OCR + análisis
                // (Reemplaza ML Kit que no existe en web)
                callCloudFunctionWithImage(
                    "Analiza esta imagen siguiendo estos pasos:\n" +
                    "1. PRIMERO extrae todo el texto visible en la imagen (OCR).\n" +
                    "2. LUEGO analiza o resuelve el contenido de manera educativa y paso a paso.\n\n" +
                    "Formato de respuesta:\n" +
                    "📝 Texto extraído (OCR):\n[texto encontrado en la imagen]\n\n" +
                    "🔍 Análisis AI:\n[tu análisis o resolución del contenido]",
                    base64Data,
                    mimeType
                )
            } else {
                "Error: No se pudo procesar la imagen adjuntada."
            }
        } catch (e: Exception) {
            "Error al analizar la imagen: ${e.message}"
        }
    }

    override suspend fun solveProblem(problem: String): String {
        return try {
            callCloudFunction(
                "Resuelve el siguiente problema de manera clara y paso a paso:\n\n$problem",
                emptyList()
            )
        } catch (e: Exception) {
            "Error al resolver el problema: ${e.message}"
        }
    }

    override suspend fun continueChat(messages: List<ChatBotMessage>, newMessage: String): String {
        return try {
            // Pasar solo el nuevo mensaje; el Cloud Function construye el prompt
            // completo a partir del conversationHistory array (evita duplicar historia)
            callCloudFunction(newMessage, messages)
        } catch (e: Exception) {
            "Error al procesar la consulta: ${e.message}"
        }
    }

    /**
     * Llama a la Cloud Function con imagen base64 para análisis multimodal.
     * Usa fetchImageWithAuth que construye el JSON en JavaScript para evitar
     * problemas de memoria con strings base64 enormes en Kotlin/Wasm.
     */
    private suspend fun callCloudFunctionWithImage(
        prompt: String,
        imageBase64: String,
        mimeType: String
    ): String {
        val idToken = firebaseGetIdToken()
        val responseText = fetchImageWithAuth(
            url = Constans.GEMINI_FUNCTION_URL,
            token = idToken,
            message = prompt,
            imageBase64 = imageBase64,
            mimeType = mimeType
        )
        return parseResponseField(responseText)
    }

    private suspend fun callCloudFunction(
        prompt: String,
        conversationHistory: List<ChatBotMessage>
    ): String {
        val idToken = firebaseGetIdToken()

        val historyJson = if (conversationHistory.isNotEmpty()) {
            val items = conversationHistory.joinToString(",") { msg ->
                """{"text":"${escapeJson(msg.text)}","isUser":${msg.isFromUser}}"""
            }
            ""","conversationHistory":[$items]"""
        } else ""

        val bodyJson = """{"message":"${escapeJson(prompt)}"$historyJson}"""

        val responseText = fetchWithAuth(Constans.GEMINI_FUNCTION_URL, idToken, bodyJson)
        return parseResponseField(responseText)
    }

    private fun escapeJson(text: String): String = text
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")

    private fun parseResponseField(jsonText: String): String {
        // Buscar "error" primero para dar mejor mensaje
        if (jsonText.contains("\"error\"") && jsonText.contains("\"success\":false")) {
            val errorKey = "\"error\":\""
            val errStart = jsonText.indexOf(errorKey)
            if (errStart != -1) {
                val errValStart = errStart + errorKey.length
                val errValEnd = jsonText.indexOf("\"", errValStart)
                if (errValEnd != -1) {
                    throw Exception(jsonText.substring(errValStart, errValEnd))
                }
            }
            throw Exception(jsonText)
        }

        val key = "\"response\":"
        val start = jsonText.indexOf(key)
        if (start == -1) throw Exception("Respuesta inesperada: $jsonText")
        val vStart = start + key.length
        if (vStart >= jsonText.length || jsonText[vStart] != '"')
            throw Exception("Formato inesperado")
        var i = vStart + 1
        val sb = StringBuilder()
        while (i < jsonText.length) {
            val c = jsonText[i]
            if (c == '\\' && i + 1 < jsonText.length) {
                when (jsonText[i + 1]) {
                    '"'  -> { sb.append('"');  i += 2 }
                    '\\' -> { sb.append('\\'); i += 2 }
                    'n'  -> { sb.append('\n'); i += 2 }
                    'r'  -> { sb.append('\r'); i += 2 }
                    't'  -> { sb.append('\t'); i += 2 }
                    else -> { sb.append(c); i++ }
                }
            } else if (c == '"') break
            else { sb.append(c); i++ }
        }
        return sb.toString()
    }

    override suspend fun queryFacturas(question: String): String {
        return try {
            val idToken = firebaseGetIdToken()
            val bodyJson = """{"question":"${escapeJson(question)}"}"""
            val responseText = fetchWithAuth(Constans.QUERY_FACTURAS_URL, idToken, bodyJson)
            parseAnswerField(responseText)  // busca "answer" en lugar de "response"
        } catch (e: Exception) {
            "Error al consultar facturas: ${e.message}"
        }
    }

    override suspend fun queryOllama(question: String): String {
        return try {
            val bodyJson = """{"pregunta":"${escapeJson(question)}"}"""
            val responseText = fetchPublic(Constans.OLLAMA_FACTURAS_URL, bodyJson)
            // Extraer campo "respuesta"
            val respuesta   = extractJsonField(responseText, "respuesta")
            val sqlGenerado = extractJsonField(responseText, "sql_generado")
            buildString {
                if (respuesta.isNotBlank())   appendLine(respuesta)
                if (sqlGenerado.isNotBlank()) appendLine("\n📊 SQL: `$sqlGenerado`")
            }.trim().ifEmpty { responseText }
        } catch (e: Exception) {
            "Error al consultar Ollama: ${e.message}"
        }
    }

    // Fetch sin auth (Ollama ngrok es público)
    private suspend fun fetchPublic(url: String, bodyJson: String): String {
        // Reusar el mismo mecanismo fetch pero sin Authorization header
        // Ajustar según tu implementación actual de fetchWithAuth
        return fetchWithAuth(url, null, bodyJson)
    }
}
