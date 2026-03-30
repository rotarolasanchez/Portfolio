package core.service

import core.utils.Constans
import di.IosViewModelHolder
import domain.model.ChatBotMessage
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.*
import kotlin.coroutines.resume

class TextRecognitionServiceImpl : TextRecognitionService {
    override suspend fun recognizeText(imageData: ByteArray): String =
        "Texto extraído vía Vision (iOS) - pendiente de integración nativa"
}

/**
 * Implementación iOS de GeminiCloudService.
 *
 * Usa el endpoint askGeminiIos que maneja Firebase Auth en el servidor.
 * iOS solo envía {email, password, message, conversationHistory}.
 * Sin ninguna API key en el cliente — igual que Android pero el servidor hace la auth.
 */
class GeminiCloudServiceImpl : GeminiCloudService {

    override suspend fun analyzeImage(bitmap: presentation.view.organisms.PlatformBitmap): String =
        callIosFunction(
            message = "Analiza la siguiente imagen y describe su contenido de manera educativa.",
            history = emptyList()
        )

    override suspend fun solveProblem(problem: String): String =
        callIosFunction(message = problem, history = emptyList())

    override suspend fun continueChat(
        messages: List<ChatBotMessage>,
        newMessage: String
    ): String = callIosFunction(message = newMessage, history = messages)

    private suspend fun callIosFunction(
        message: String,
        history: List<ChatBotMessage>
    ): String = withContext(Dispatchers.Default) {

        val email    = IosViewModelHolder.savedEmail
            ?: return@withContext "Error: no hay sesión activa. Por favor inicia sesión."
        val password = IosViewModelHolder.savedPassword
            ?: return@withContext "Error: no hay sesión activa. Por favor inicia sesión."

        val body = buildRequestJson(email, password, message, history)

        try {
            val responseText = postRequest(Constans.GEMINI_FUNCTION_URL, body)
            extractJsonField(responseText, "response")
                ?: extractJsonField(responseText, "error")
                ?: "Error: respuesta inesperada del servidor."
        } catch (e: Exception) {
            println("[iOS] GeminiCloudService error: ${e.message}")
            "Error al conectar con el servidor: ${e.message}"
        }
    }

    private fun buildRequestJson(
        email: String,
        password: String,
        message: String,
        history: List<ChatBotMessage>
    ): String {
        fun String.esc() = replace("\\", "\\\\").replace("\"", "\\\"")
            .replace("\n", "\\n").replace("\r", "\\r")

        return buildString {
            append("{")
            append("\"email\":\"${email.esc()}\",")
            append("\"password\":\"${password.esc()}\",")
            append("\"message\":\"${message.esc()}\"")
            if (history.isNotEmpty()) {
                append(",\"conversationHistory\":[")
                history.forEachIndexed { i, msg ->
                    append("{\"text\":\"${msg.text.esc()}\",\"isUser\":${msg.isFromUser}}")
                    if (i < history.size - 1) append(",")
                }
                append("]")
            }
            append("}")
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private suspend fun postRequest(url: String, body: String): String {
        val nsUrl = NSURL.URLWithString(url) ?: throw Exception("URL inválida")
        val request = NSMutableURLRequest.requestWithURL(nsUrl).apply {
            HTTPMethod = "POST"
            setValue("application/json", forHTTPHeaderField = "Content-Type")
            HTTPBody = body.encodeToByteArray().toNSData()
            timeoutInterval = 30.0
        }
        return suspendCancellableCoroutine<String> { cont ->
            NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
                if (error != null || data == null) {
                    cont.resume("Error: ${error?.localizedDescription ?: "sin datos"}")
                    return@dataTaskWithRequest
                }
                val httpStatus = (response as? NSHTTPURLResponse)?.statusCode ?: 0
                println("[iOS] askGeminiIos status: $httpStatus")
                val text = NSString.create(data, NSUTF8StringEncoding)?.toString() ?: ""
                cont.resume(text)
            }.resume()
        }
    }

    private fun extractJsonField(json: String, field: String): String? {
        val key = "\"$field\":\""
        val start = json.indexOf(key).takeIf { it >= 0 } ?: return null
        val valueStart = start + key.length
        val valueEnd   = json.indexOf('"', valueStart).takeIf { it >= 0 } ?: return null
        return json.substring(valueStart, valueEnd).ifBlank { null }
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun ByteArray.toNSData(): NSData =
    if (isEmpty()) NSData()
    else usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
