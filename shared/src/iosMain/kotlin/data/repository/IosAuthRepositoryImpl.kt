package data.repository

import core.utils.Constans
import di.IosViewModelHolder
import domain.model.UserModel
import domain.repositories.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

/**
 * Implementación iOS de AuthRepository.
 *
 * Llama a Firebase Auth REST API para obtener un ID Token real,
 * el cual se envía como Bearer token a la Cloud Function de Gemini.
 * Esto es equivalente a lo que hace Android con el Firebase SDK.
 */
class IosAuthRepositoryImpl : AuthRepository {

    private val client = HttpClient(Darwin)

    private var currentUser: UserModel? = null

    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        if (email.isBlank() || password.length < 6) {
            return Result.failure(Exception("Email o contraseña inválidos"))
        }

        // Guardar credenciales en memoria (para re-auth si el token expira)
        IosViewModelHolder.savedEmail    = email
        IosViewModelHolder.savedPassword = password

        return try {
            // Llamar a Firebase Auth REST API para obtener un ID Token real
            val idToken = fetchFirebaseIdToken(email, password)

            if (idToken != null) {
                IosViewModelHolder.firebaseIdToken = idToken
                println("[iOS] Auth: token Firebase obtenido para $email")
            } else {
                // Sin token (API key placeholder) — modo desarrollo
                println("[iOS] Auth: sin token Firebase, usando email+password en Cloud Function")
            }

            val user = UserModel(
                id       = "ios-${email.hashCode()}",
                email    = email,
                userName = email.substringBefore("@"),
                userCode = email
            )
            currentUser = user
            Result.success(user)

        } catch (e: Exception) {
            println("[iOS] Auth error: ${e.message}")
            // Fallar solo si las credenciales son incorrectas según Firebase
            if (e.message?.contains("INVALID_PASSWORD") == true ||
                e.message?.contains("EMAIL_NOT_FOUND") == true ||
                e.message?.contains("INVALID_LOGIN_CREDENTIALS") == true) {
                Result.failure(Exception("Email o contraseña incorrectos"))
            } else {
                // Error de red o config → continuar con email+password como fallback
                println("[iOS] Auth: usando modo fallback email+password")
                val user = UserModel(
                    id       = "ios-${email.hashCode()}",
                    email    = email,
                    userName = email.substringBefore("@"),
                    userCode = email
                )
                currentUser = user
                Result.success(user)
            }
        }
    }

    /**
     * Llama a Firebase Auth REST API y retorna el idToken, o null si
     * FIREBASE_WEB_API_KEY aún es el placeholder.
     */
    private suspend fun fetchFirebaseIdToken(email: String, password: String): String? {
        val apiKey = Constans.FIREBASE_WEB_API_KEY
        println("[iOS] fetchFirebaseIdToken: apiKey='${apiKey.take(15)}...' (length=${apiKey.length})")
        
        if (apiKey.isBlank() || apiKey.contains("HERE")) {
            println("[iOS] FIREBASE_WEB_API_KEY no configurado — usando fallback email+password")
            return null
        }

        val url = "${Constans.FIREBASE_AUTH_URL}?key=$apiKey"
        println("[iOS] Firebase Auth URL: $url")
        val body = """{"email":"$email","password":"$password","returnSecureToken":true}"""

        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

        val responseText = response.bodyAsText()
        println("[iOS] Firebase Auth response: ${response.status.value} — ${responseText.take(150)}...")

        if (!response.status.isSuccess()) {
            // Lanzar con el error de Firebase para que el llamador lo detecte
            throw Exception(responseText)
        }

        val token = parseJsonField(responseText, "idToken")
        println("[iOS] Extracted idToken: ${token?.take(50)}... (length=${token?.length})")
        return token
    }

    override suspend fun signInWithGoogle(): Result<UserModel> =
        Result.failure(Exception("Google Sign-In no disponible en iOS"))

    override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> =
        Result.failure(Exception("Registro no disponible en iOS"))

    override suspend fun signOut(): Result<Unit> {
        currentUser = null
        IosViewModelHolder.savedEmail      = null
        IosViewModelHolder.savedPassword   = null
        IosViewModelHolder.firebaseIdToken = null
        println("[iOS] AuthRepository: sesión cerrada")
        return Result.success(Unit)
    }

    override fun getCurrentUser(): UserModel? = currentUser

    private fun parseJsonField(json: String, field: String): String? {
        val key = "\"$field\":\""
        val start = json.indexOf(key).takeIf { it >= 0 } ?: return null
        val valueStart = start + key.length
        val valueEnd = json.indexOf('"', valueStart).takeIf { it >= 0 } ?: return null
        return json.substring(valueStart, valueEnd)
    }
}
