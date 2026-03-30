package di

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy
import presentation.viewmodels.AuthViewModel
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel
import org.jetbrains.skia.Image as SkiaImage

/**
 * Holder de ViewModels para iOS.
 * Evita depender de composition locals de Koin (que fallan en Kotlin/Native).
 * Los ViewModels se inicializan una sola vez desde MainViewController.
 */
object IosViewModelHolder {
    var chatBotViewModel: ChatBotViewModel? = null
    var authViewModel: AuthViewModel? = null
    var menuViewModel: MenuViewModel? = null

    /** Firebase ID Token — obtenido vía REST API al hacer login */
    var firebaseIdToken: String? = null
    /** Credenciales en memoria para re-autenticar si el token expira */
    var savedEmail: String? = null
    var savedPassword: String? = null

    /** Bitmaps precargados al inicio — disponibles desde el primer frame de Compose */
    val preloadedBitmaps = mutableMapOf<String, ImageBitmap>()

    fun isInitialized() = chatBotViewModel != null

    @OptIn(ExperimentalForeignApi::class)
    fun preloadBitmaps() {
        val resourcePaths = listOf(
            "drawable/capibara_family_not_background.png"
        )
        val bundlePath = NSBundle.mainBundle.bundlePath
        for (path in resourcePaths) {
            runCatching {
                val fullPath = "$bundlePath/composeResources/portafolio_kotlin.shared.generated.resources/$path"
                val data = NSData.dataWithContentsOfFile(fullPath)
                    ?: throw IllegalStateException("Archivo no encontrado: $fullPath")

                val bytes = ByteArray(data.length.toInt())
                bytes.usePinned { pinned ->
                    memcpy(pinned.addressOf(0), data.bytes, data.length)
                }

                val skiaImage = SkiaImage.makeFromEncoded(bytes)
                    ?: throw IllegalStateException("No se pudo decodificar la imagen: $path")

                preloadedBitmaps[path] = skiaImage.toComposeImageBitmap()
                println("IosViewModelHolder: ✅ precargado $path (${bytes.size} bytes)")
            }.onFailure { e ->
                println("IosViewModelHolder: ❌ error precargando $path → ${e.message}")
            }
        }
    }
}
