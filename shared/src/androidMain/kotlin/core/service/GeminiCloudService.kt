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


    private val bigQueryClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val ollamaClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .connectionPool(okhttp3.ConnectionPool(0, 1, TimeUnit.MILLISECONDS)) // sin pool: nueva conexión siempre
        .retryOnConnectionFailure(true)
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
            // Pasar solo el nuevo mensaje; el Cloud Function construye el prompt
            // completo a partir del conversationHistory array (evita duplicar historia)
            callCloudFunction(newMessage, messages)
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

    override suspend fun queryFacturas(question: String): String {
        return try {
            callQueryFacturasFunction(question)
        } catch (e: Exception) {
            "Error al consultar facturas: ${e.message}"
        }
    }

    private suspend fun callQueryFacturasFunction(question: String): String = withContext(Dispatchers.IO) {
        val idToken = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            ?: throw Exception("Usuario no autenticado")

        val json = JSONObject().apply { put("question", question) }
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(Constans.QUERY_FACTURAS_URL)  // ← agregar esta constante
            .post(requestBody)
            .addHeader("Authorization", "Bearer $idToken")
            .build()

        val response = bigQueryClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: "{}"
        if (!response.isSuccessful) {
            Log.e(ContentValues.TAG, "queryFacturas HTTP Error: ${response.code} - $responseBody")
            throw Exception("HTTP ${response.code}")
        }

        JSONObject(responseBody).getString("answer")
    }

    override suspend fun queryOllama(question: String): String {
        return try {
            // Verificar conectividad antes de intentar la consulta
            checkOllamaHealth()
            callOllamaWithRetry(question, maxRetries = 3)
        } catch (e: Exception) {
            Log.e("GeminiCloudService", "queryOllama error: ${e.javaClass.simpleName} — ${e.message}", e)
            when {
                e.message?.contains("400") == true ->
                    "❌ El agente no pudo generar una consulta válida para esa pregunta. Intenta reformularla de otra forma."
                e.message?.contains("timeout", ignoreCase = true) == true ||
                e.message?.contains("SocketTimeoutException", ignoreCase = true) == true ->
                    "⏱️ La consulta tardó demasiado. Intenta con una pregunta más específica."
                e.message?.contains("401") == true ->
                    "🔐 Error de autenticación. Vuelve a iniciar sesión."
                e.message?.contains("servidor no disponible", ignoreCase = true) == true ||
                e.message?.contains("unreachable", ignoreCase = true) == true ->
                    "🔌 Servidor Ollama no disponible en ${Constans.OLLAMA_FACTURAS_URL}\nVerifica que el servidor esté corriendo y que el celular esté en la misma red WiFi."
                e.message?.contains("ConnectException", ignoreCase = true) == true ||
                e.message?.contains("Failed to connect", ignoreCase = true) == true ->
                    "🔌 No se pudo conectar al servidor Ollama (${Constans.OLLAMA_FACTURAS_URL.substringBefore("/query/")}).\nVerifica que: 1) el servidor FastAPI esté corriendo, 2) el celular esté en la misma WiFi, 3) la IP sea correcta."
                e.message?.contains("abort", ignoreCase = true) == true ||
                e.message?.contains("reset", ignoreCase = true) == true ||
                e.message?.contains("connection", ignoreCase = true) == true ->
                    "🔄 Conexión interrumpida con el servidor Ollama.\nURL: ${Constans.OLLAMA_FACTURAS_URL}\nDetalle: ${e.message}"
                else ->
                    "❌ Error al consultar Ollama: ${e.javaClass.simpleName} — ${e.message}"
            }
        }
    }

    private suspend fun checkOllamaHealth(): Unit = withContext(Dispatchers.IO) {
        val healthUrl = Constans.OLLAMA_FACTURAS_URL
            .substringBefore("/query/")
            .let { base -> "$base/health" }
        try {
            val request = Request.Builder()
                .url(healthUrl)
                .get()
                .build()
            val response = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()
                .newCall(request).execute()
            response.close()
            if (!response.isSuccessful && response.code != 404) {
                // 404 es aceptable (el servidor existe pero el endpoint /health no)
                Log.w("GeminiCloudService", "Health check respondió ${response.code} — continuando de todas formas")
            }
        } catch (e: Exception) {
            Log.e("GeminiCloudService", "Health check falló: ${e.message}")
            throw Exception("servidor no disponible: ${e.message}")
        }
    }

    private suspend fun callOllamaWithRetry(question: String, maxRetries: Int): String {
        var lastException: Exception? = null
        repeat(maxRetries) { attempt ->
            try {
                return callOllamaFunction(question)
            } catch (e: Exception) {
                lastException = e
                val isRetryable = e.message?.contains("abort", ignoreCase = true) == true ||
                    e.message?.contains("reset", ignoreCase = true) == true ||
                    e.message?.contains("connection", ignoreCase = true) == true
                if (!isRetryable || attempt == maxRetries - 1) throw e
                Log.w("GeminiCloudService", "Ollama intento ${attempt + 1} fallido, reintentando: ${e.message}")
                kotlinx.coroutines.delay(1500L * (attempt + 1)) // backoff: 1.5s, 3s
            }
        }
        throw lastException ?: Exception("Error desconocido")
    }

    private suspend fun callOllamaFunction(question: String): String = withContext(Dispatchers.IO) {
        val idToken = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            ?: throw Exception("Usuario no autenticado")

        // El servidor FastAPI local espera el campo "pregunta"
        val json = JSONObject().apply { put("pregunta", question) }
        val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(Constans.OLLAMA_FACTURAS_URL)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $idToken")
            .addHeader("Content-Type", "application/json")
            .addHeader("ngrok-skip-browser-warning", "true")
            .addHeader("User-Agent", "PortafolioKMP/1.0")
            .build()

        val response = ollamaClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: "{}"
        if (!response.isSuccessful) throw Exception("HTTP ${response.code}: $responseBody")

        val jsonResp = JSONObject(responseBody)
        val respuesta   = jsonResp.optString("respuesta", "")
        val sqlGenerado = jsonResp.optString("sql_generado", "")

        buildString {
            if (respuesta.isNotBlank())   appendLine(respuesta)
            if (sqlGenerado.isNotBlank()) appendLine("\n📊 SQL: `$sqlGenerado`")
        }.trim()
    }
}