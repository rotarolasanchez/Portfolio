package core.interop
import kotlinx.coroutines.delay
/**
 * Interop con Firebase JS SDK y fetch API desde Kotlin/Wasm.
 *
 * REGLAS para @JsFun en WasmJs (Kotlin 2.1.20):
 * - Tipos de retorno nullable: usar JsAny? (NO JsString? ni String?)
 * - NO pasar tipos de funcion Kotlin como parametros en external fun
 * - Usar polling con delay() para Promises (no callbacks)
 */
// ============ Declaraciones externas JS ============
@JsFun("(email, password) => window.firebaseSignIn(email, password)")
external fun firebaseSignInJs(email: String, password: String): JsAny
@JsFun("() => window.firebaseGetIdToken()")
external fun firebaseGetIdTokenJs(): JsAny
@JsFun("() => window.firebaseSignOut()")
external fun firebaseSignOutJs(): JsAny
// FIX: JsAny? en lugar de JsString? - los nullable JS deben ser JsAny? en WasmJs 2.1.20
@JsFun("() => window.firebaseGetCurrentUser()")
external fun firebaseGetCurrentUserJs(): JsAny?
@JsFun("(url, token, body) => window.fetchWithAuth(url, token, body)")
external fun fetchWithAuthJs(url: String, token: String, body: String): JsAny
@JsFun("(url, secret, body) => window.fetchWithSecret(url, secret, body)")
external fun fetchWithSecretJs(url: String, secret: String, body: String): JsAny
@JsFun("(url, token, message, imageBase64, mimeType) => window.fetchImageWithAuth(url, token, message, imageBase64, mimeType)")
external fun fetchImageWithAuthJs(url: String, token: String, message: String, imageBase64: String, mimeType: String): JsAny
@JsFun("() => window.pickImageFile()")
external fun pickImageFileJs(): JsAny
// FIX: JsAny? en lugar de JsString? para return type nullable
@JsFun("(promise) => window._ktRegisterPromise(promise)")
private external fun registerPromiseJs(promise: JsAny): Int
@JsFun("(id) => window._ktPollPromise(id)")
private external fun pollPromiseJs(id: Int): JsAny?
// ============ Funciones suspend Kotlin ============
suspend fun firebaseSignIn(email: String, password: String): String =
    awaitJsPromise(firebaseSignInJs(email, password))
suspend fun firebaseGetIdToken(): String =
    awaitJsPromise(firebaseGetIdTokenJs())
suspend fun firebaseSignOut() {
    awaitJsPromise(firebaseSignOutJs())
}
// FIX: uso de JsAny? en lugar de JsString?
fun firebaseGetCurrentUser(): String? =
    firebaseGetCurrentUserJs()?.toString()
suspend fun fetchWithAuth(url: String, token: String, body: String): String =
    awaitJsPromise(fetchWithAuthJs(url, token, body))
suspend fun fetchWithSecret(url: String, secret: String, body: String): String =
    awaitJsPromise(fetchWithSecretJs(url, secret, body))
suspend fun fetchImageWithAuth(
    url: String, token: String, message: String,
    imageBase64: String, mimeType: String
): String = awaitJsPromise(fetchImageWithAuthJs(url, token, message, imageBase64, mimeType))
suspend fun pickImageFile(): String? = try {
    awaitJsPromise(pickImageFileJs())
} catch (e: Exception) {
    null
}
// ============ Await helper via polling ============
private suspend fun awaitJsPromise(promise: JsAny): String {
    val id = registerPromiseJs(promise)
    while (true) {
        val result = pollPromiseJs(id)
        if (result != null) {
            val str = result.toString()
            return if (str.startsWith("E:")) throw Exception(str.removePrefix("E:"))
            else str.removePrefix("S:")
        }
        delay(1)
    }
}