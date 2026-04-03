        // Precargar imágenes PNG sincrónicamente antes del primer frame de Compose
        // El bundle está en local → runBlocking es seguro y rápido aquí
        runBlocking {
            IosViewModelHolder.preloadBitmaps()
import androidx.compose.ui.window.ComposeUIViewController
import di.IosViewModelHolder
import di.commonModule
@Suppress("unused") // llamada desde Swift: MainiOSKt.MainViewController()
import di.iosModule
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    if (!IosViewModelHolder.isInitialized()) {
        val koin = startKoin {
        // Precargar imágenes usando Foundation directamente (síncrono, sin corrutinas)
        IosViewModelHolder.preloadBitmaps()

        // Precargar imágenes PNG sincrónicamente antes del primer frame de Compose
        // El bundle está en local → runBlocking es seguro y rápido aquí
        runBlocking {
            IosViewModelHolder.preloadBitmaps()
        }
    }

    return ComposeUIViewController {
        App()
    }
}
