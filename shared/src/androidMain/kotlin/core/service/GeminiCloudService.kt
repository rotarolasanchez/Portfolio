package core.service

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import core.utils.Constans
import domain.model.ChatBotMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import core.model.PlatformBitmap
import java.util.concurrent.TimeUnit

class GeminiCloudServiceImpl : GeminiCloudService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
        return try {
            val prompt = """
            Analiza la siguiente imagen y describe su contenido de manera educativa.
            """.trimIndent()

            callCloudFunction(prompt, emptyList())
        } catch (e: Exception) {
            Log.e("GeminiCloudService", "Error analyzing image", e)
            "Error al analizar la imagen: ${e.message}"
        }
    }

    override suspend fun solveProblem(problem: String): String {
        return try {
            val prompt = """
            Resuelve el siguiente problema matemático o educativo de manera clara y paso a paso:
            
            $problem
            """.trimIndent()

            callCloudFunction(prompt, emptyList())
        } catch (e: Exception) {
            Log.e("GeminiCloudService", "Error solving problem", e)
            "Error al resolver el problema: ${e.message}"
        }
    }

    override suspend fun continueChat(
        messages: List<ChatBotMessage>,
        newMessage: String
    ): String {
        return try {
            val historyText = messages.joinToString("\n") { message ->
                if (message.isFromUser) "Usuario: ${message.text}"
                else "Asistente: ${message.text}"
            }

            val prompt = """
            Historial de conversación:
            $historyText

            Nueva pregunta del usuario: $newMessage

            Responde de manera educativa y útil, manteniendo el contexto de la conversación anterior.
            """.trimIndent()

            callCloudFunction(prompt, messages)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error continuing chat", e)
            "Error al procesar la consulta: ${e.message}"
        }
    }

    // ✅ CAMBIO: Agregar parámetro conversationHistory
    private suspend fun callCloudFunction(
        prompt: String,
        conversationHistory: List<ChatBotMessage>
    ): String = withContext(Dispatchers.IO) {
        val url = Constans.GEMINI_FUNCTION_URL

        val idToken = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            ?: throw Exception("Usuario no autenticado")

        val json = JSONObject().apply {
            put("message", prompt)  // ✅ Cambiar "prompt" por "message"

            if (conversationHistory.isNotEmpty()) {
                val historyArray = JSONArray()
                conversationHistory.forEach { message ->
                    val messageJson = JSONObject().apply {
                        put("text", message.text)
                        put("isUser", message.isFromUser)  // ✅ Cambiar "isFromUser" por "isUser"
                    }
                    historyArray.put(messageJson)
                }
                put("conversationHistory", historyArray)  // ✅ Cambiar "history" por "conversationHistory"
            }
        }

        val requestBody = json.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $idToken")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: "{}"

        if (!response.isSuccessful) {
            Log.e(ContentValues.TAG, "HTTP Error: ${response.code} - $responseBody")
            throw Exception("Error en la llamada HTTP: ${response.code}")
        }

        val jsonResponse = JSONObject(responseBody)
        jsonResponse.getString("response")
    }
}