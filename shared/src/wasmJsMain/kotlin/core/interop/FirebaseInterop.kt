package core.interop

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Interop con Firebase JS SDK y fetch API desde Kotlin/Wasm
 * Las funciones JS están definidas en index.html como propiedades de window
 */

// ============ Declaraciones externas JS ============

@JsFun("(email, password) => window.firebaseSignIn(email, password)")
external fun firebaseSignInJs(email: String, password: String): JsAny // Returns Promise<String>

@JsFun("() => window.firebaseGetIdToken()")
external fun firebaseGetIdTokenJs(): JsAny // Returns Promise<String>

@JsFun("() => window.firebaseSignOut()")
external fun firebaseSignOutJs(): JsAny // Returns Promise

@JsFun("() => window.firebaseGetCurrentUser()")
external fun firebaseGetCurrentUserJs(): JsString? // Returns String? (JSON)

@JsFun("(url, token, body) => window.fetchWithAuth(url, token, body)")
external fun fetchWithAuthJs(url: String, token: String, body: String): JsAny

@JsFun("(url, secret, body) => window.fetchWithSecret(url, secret, body)")
external fun fetchWithSecretJs(url: String, secret: String, body: String): JsAny

@JsFun("(url, token, message, imageBase64, mimeType) => window.fetchImageWithAuth(url, token, message, imageBase64, mimeType)")
external fun fetchImageWithAuthJs(url: String, token: String, message: String, imageBase64: String, mimeType: String): JsAny

@JsFun("() => window.pickImageFile()")
external fun pickImageFileJs(): JsAny // Returns Promise<String> (data URL base64)

// ============ Helper para convertir Promise JS a suspend ============

@JsFun("(promise, onResolve, onReject) => promise.then(v => onResolve(v), e => onReject(String(e)))")
external fun thenPromise(promise: JsAny, onResolve: (JsString) -> Unit, onReject: (JsString) -> Unit)

@JsFun("(promise, onResolve, onReject) => promise.then(() => onResolve(), e => onReject(String(e)))")
external fun thenPromiseUnit(promise: JsAny, onResolve: () -> Unit, onReject: (JsString) -> Unit)

// ============ Funciones suspend Kotlin ============

suspend fun firebaseSignIn(email: String, password: String): String {
    val promise = firebaseSignInJs(email, password)
    return awaitJsPromise(promise)
}

suspend fun firebaseGetIdToken(): String {
    val promise = firebaseGetIdTokenJs()
    return awaitJsPromise(promise)
}

suspend fun firebaseSignOut() {
    val promise = firebaseSignOutJs()
    awaitJsPromiseUnit(promise)
}

fun firebaseGetCurrentUser(): String? {
    return firebaseGetCurrentUserJs()?.toString()
}

suspend fun fetchWithAuth(url: String, token: String, body: String): String {
    val promise = fetchWithAuthJs(url, token, body)
    return awaitJsPromise(promise)
}

suspend fun fetchWithSecret(url: String, secret: String, body: String): String {
    val promise = fetchWithSecretJs(url, secret, body)
    return awaitJsPromise(promise)
}

/**
 * Envía una imagen base64 a la Cloud Function usando JSON.stringify en JS,
 * evitando construir el string JSON enorme en Kotlin/Wasm.
 */
suspend fun fetchImageWithAuth(url: String, token: String, message: String, imageBase64: String, mimeType: String): String {
    val promise = fetchImageWithAuthJs(url, token, message, imageBase64, mimeType)
    return awaitJsPromise(promise)
}

/**
 * Abre el selector de archivos del navegador para elegir una imagen.
 * Retorna la imagen como data URL (data:image/...;base64,...) o null si se cancela.
 */
suspend fun pickImageFile(): String? {
    return try {
        val promise = pickImageFileJs()
        awaitJsPromise(promise)
    } catch (e: Exception) {
        null // Cancelado o error
    }
}

// ============ Await helpers ============

private suspend fun awaitJsPromise(promise: JsAny): String {
    return suspendCancellableCoroutine { cont ->
        thenPromise(
            promise,
            onResolve = { value ->
                cont.resume(value.toString())
            },
            onReject = { error ->
                cont.resumeWithException(Exception(error.toString()))
            }
        )
    }
}

private suspend fun awaitJsPromiseUnit(promise: JsAny) {
    return suspendCancellableCoroutine { cont ->
        thenPromiseUnit(
            promise,
            onResolve = {
                cont.resume(Unit)
            },
            onReject = { error ->
                cont.resumeWithException(Exception(error.toString()))
            }
        )
    }
}

