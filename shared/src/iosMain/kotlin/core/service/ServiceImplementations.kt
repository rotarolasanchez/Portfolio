package core.service

import domain.model.ChatBotMessage
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.dataUsingEncoding
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.create
import platform.Foundation.setHTTPBody
import platform.Foundation.setHTTPMethod
import platform.Foundation.setValue
import presentation.view.organisms.PlatformBitmap
import core.utils.Constans
import kotlin.coroutines.resume

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
 * Usa NSURLSession (API nativa iOS/macOS) para llamar a la Cloud Function.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class GeminiCloudServiceImpl : GeminiCloudService {

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
        return try {
            val prompt = """
            Analiza esta imagen y describe su contenido de manera educativa.
            Si contiene texto, extráelo. Si contiene problemas, resuélvelos paso a paso.
            """.trimIndent()
            callCloudFunction(prompt, emptyList())
        } catch (e: Exception) {
            "Error al analizar la imagen en iOS: ${e.message}"
        }
    }

    override suspend fun solveProblem(problem: String): String {
        return try {
            callCloudFunction(
                "Resuelve el siguiente problema de manera clara y paso a paso:\n\n$problem",
                emptyList()
            )
        } catch (e: Exception) {
            "Error al resolver el problema en iOS: ${e.message}"
        }
    }

    override suspend fun continueChat(
        messages: List<ChatBotMessage>,
        newMessage: String
    ): String {
        return try {
            val historyText = messages.joinToString("\n") { msg ->
                if (msg.isFromUser) "Usuario: ${msg.text}" else "Asistente: ${msg.text}"
            }
            val prompt = if (historyText.isNotEmpty()) {
                "$historyText\n\nNueva pregunta: $newMessage\n\nResponde de manera educativa."
            } else {
                "Pregunta: $newMessage\n\nResponde de manera educativa."
            }
            callCloudFunction(prompt, messages)
        } catch (e: Exception) {
            "Error al procesar la consulta en iOS: ${e.message}"
        }
    }

    /**
     * Llama a la Cloud Function usando NSURLSession con API correcta de Kotlin/Native.
     */
    private suspend fun callCloudFunction(
        prompt: String,
        conversationHistory: List<ChatBotMessage>
    ): String = withContext(Dispatchers.Default) {

        val url = NSURL.URLWithString(Constans.GEMINI_FUNCTION_URL)
            ?: throw Exception("URL inválida: ${Constans.GEMINI_FUNCTION_URL}")

        val request = NSMutableURLRequest.requestWithURL(url) as NSMutableURLRequest
        request.setHTTPMethod("POST")
        request.setValue("application/json", forHTTPHeaderField = "Content-Type")

        val historyJson = if (conversationHistory.isNotEmpty()) {
            val items = conversationHistory.joinToString(",") { msg ->
                """{"text":"${escapeJson(msg.text)}","isUser":${msg.isFromUser}}"""
            }
            ""","conversationHistory":[$items]"""
        } else ""

        val bodyJson = """{"message":"${escapeJson(prompt)}"$historyJson}"""

        // Convertir String de Kotlin a NSData via ByteArray
        val bodyBytes = bodyJson.encodeToByteArray()
        val bodyData = bodyBytes.usePinned { pinned ->
            NSData.create(
                bytes = pinned.addressOf(0),
                length = bodyBytes.size.toULong()
            )
        }
        request.setHTTPBody(bodyData)

        // Ejecutar la petición HTTP con callback suspendible
        val (data, response, error) = suspendNSURLSession(request)

        if (error != null) {
            throw Exception("Error de red iOS: ${error.localizedDescription}")
        }

        val httpResponse = response as? NSHTTPURLResponse
        val statusCode = httpResponse?.statusCode?.toInt() ?: 0
        if (statusCode != 200) {
            throw Exception("HTTP $statusCode desde Cloud Function")
        }

        // Convertir NSData a String de Kotlin via ByteArray
        val responseString = data?.let { nsData ->
            val bytes = ByteArray(nsData.length.toInt())
            bytes.usePinned { pinned ->
                platform.posix.memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
            }
            bytes.decodeToString()
        } ?: throw Exception("Respuesta vacía del servidor")

        parseResponseField(responseString)
    }

    private fun escapeJson(text: String): String = text
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")

    private fun parseResponseField(jsonText: String): String {
        val key = "\"response\":"
        val start = jsonText.indexOf(key)
        if (start == -1) throw Exception("Respuesta inesperada: $jsonText")
        val vStart = start + key.length
        if (vStart >= jsonText.length || jsonText[vStart] != '"')
            throw Exception("Formato inesperado en respuesta iOS")
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
}

/**
 * Wrapper de corrutina para NSURLSessionDataTask con completionHandler.
 * Usa la sobrecarga correcta de dataTaskWithRequest que acepta lambda.
 */
@OptIn(ExperimentalForeignApi::class)
private suspend fun suspendNSURLSession(
    request: NSMutableURLRequest
): Triple<NSData?, platform.Foundation.NSURLResponse?, platform.Foundation.NSError?> =
    suspendCancellableCoroutine { continuation ->
        NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
                continuation.resume(Triple(data, response, error))
            }.resume()
    }
