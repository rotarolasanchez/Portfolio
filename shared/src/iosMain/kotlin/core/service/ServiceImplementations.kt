package core.service

import core.model.PlatformBitmap
import core.utils.Constans
import di.IosViewModelHolder
import domain.model.ChatBotMessage
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess

/**
 * Implementación iOS de TextRecognitionService.
 * Stub funcional — para OCR real usar Vision framework con wrapper Swift.
 */
class TextRecognitionServiceImpl : TextRecognitionService {
    override suspend fun recognizeText(imageData: ByteArray): String =
        "Texto extraído vía Vision (iOS) - pendiente de integración nativa"
}

/**
 * Implementación iOS de GeminiCloudService usando Ktor (Darwin engine).
 * Autentica con email+password directamente en la Cloud Function
 * (no requiere Firebase SDK en iOS).
 */
class GeminiCloudServiceImpl : GeminiCloudService {

    private val client = HttpClient(Darwin) {
        engine {
            configureRequest {
                setTimeoutInterval(30.0)
            }
        }
    }

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String =
        callCloudFunction(
            "Analiza esta imagen y describe su contenido de manera educativa.",
            emptyList()
        )

    override suspend fun solveProblem(problem: String): String =
        callCloudFunction(problem, emptyList())

    override suspend fun continueChat(
        messages: List<ChatBotMessage>,
        newMessage: String
    ): String = callCloudFunction(newMessage, messages)

    // ── Internals ─────────────────────────────────────────────────────────────

    private suspend fun callCloudFunction(
        message: String,
        history: List<ChatBotMessage>
    ): String {
        val email    = IosViewModelHolder.savedEmail
        val password = IosViewModelHolder.savedPassword
        val idToken  = IosViewModelHolder.firebaseIdToken

        println("[iOS] callCloudFunction: email=$email, hasPassword=${password != null}, hasToken=${idToken != null}")

        if (email == null || password == null) {
            return "Error: inicia sesión primero."
        }

        return try {
            // Primero intentar con Bearer token si está disponible
            if (idToken != null) {
                println("[iOS] Intentando con Bearer token...")
                val response = client.post(Constans.GEMINI_FUNCTION_URL) {
                    contentType(ContentType.Application.Json)
                    headers { append(HttpHeaders.Authorization, "Bearer $idToken") }
                    setBody(buildRequestJsonBearer(message, history))
                }

                val responseText = response.bodyAsText()
                println("[iOS] Bearer response: ${response.status.value} — ${responseText.take(150)}")

                if (response.status.isSuccess()) {
                    return parseResponseField(responseText)
                }

                // Si falla con 401, el token es inválido - limpiar y usar fallback
                if (response.status.value == 401) {
                    println("[iOS] Bearer token inválido/expirado, usando fallback email+password")
                    IosViewModelHolder.firebaseIdToken = null
                }
            }

            // Fallback: usar email+password en el body
            println("[iOS] Usando fallback email+password...")
            val bodyJson = buildRequestJson(message, email, password, history)
            println("[iOS] Fallback body: ${bodyJson.take(200)}...")

            val response = client.post(Constans.GEMINI_FUNCTION_URL) {
                contentType(ContentType.Application.Json)
                setBody(bodyJson)
            }

            val responseText = response.bodyAsText()
            println("[iOS] Fallback response: ${response.status.value} — ${responseText.take(200)}")

            if (response.status.isSuccess()) {
                parseResponseField(responseText)
            } else {
                "Error del servidor (${response.status.value}): $responseText"
            }
        } catch (e: Exception) {
            println("[iOS] GeminiService error: ${e.message}")
            "Error al contactar con Gemini: ${e.message}"
        }
    }

    private fun buildRequestJsonBearer(
        message: String,
        history: List<ChatBotMessage>
    ): String {
        fun esc(s: String) = s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")

        val histJson = buildString {
            append("[")
            history.forEachIndexed { i, m ->
                if (i > 0) append(",")
                append("""{"text":"${esc(m.text)}","isUser":${m.isFromUser}}""")
            }
            append("]")
        }
        return """{"message":"${esc(message)}","conversationHistory":$histJson}"""
    }

    private fun buildRequestJson(
        message: String,
        email: String,
        password: String,
        history: List<ChatBotMessage>
    ): String {
        fun esc(s: String) = s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")

        val histJson = buildString {
            append("[")
            history.forEachIndexed { i, m ->
                if (i > 0) append(",")
                append("""{"text":"${esc(m.text)}","isUser":${m.isFromUser}}""")
            }
            append("]")
        }
        return """{"message":"${esc(message)}","email":"${esc(email)}","password":"${esc(password)}","conversationHistory":$histJson}"""
    }

    /** Extrae el valor del campo "response" del JSON de respuesta */
    private fun parseResponseField(json: String): String {
        val key = "\"response\":"
        val keyIdx = json.indexOf(key).takeIf { it >= 0 } ?: return json
        val q1 = json.indexOf('"', keyIdx + key.length).takeIf { it >= 0 } ?: return json
        val sb = StringBuilder()
        var i = q1 + 1
        while (i < json.length) {
            when {
                json[i] == '\\' && i + 1 < json.length -> {
                    sb.append(when (json[i + 1]) {
                        '"'  -> '"'
                        'n'  -> '\n'
                        '\\' -> '\\'
                        't'  -> '\t'
                        'r'  -> '\r'
                        else -> json[i + 1]
                    })
                    i += 2
                }
                json[i] == '"' -> break
                else -> { sb.append(json[i]); i++ }
            }
        }
        return sb.toString().ifEmpty { json }
    }
}
