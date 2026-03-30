import androidx.compose.ui.window.ComposeUIViewController
import di.IosViewModelHolder
import di.commonModule
import di.iosModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

@Suppress("unused") // llamada desde Swift: MainiOSKt.MainViewController()
fun MainViewController(): UIViewController {
    if (!IosViewModelHolder.isInitialized()) {
        val koin = startKoin {
            modules(commonModule, iosModule)
        }.koin
        IosViewModelHolder.chatBotViewModel = koin.get()
        IosViewModelHolder.authViewModel    = koin.get()
        IosViewModelHolder.menuViewModel    = koin.get()

        // Precargar imágenes usando Foundation directamente (síncrono, sin corrutinas)
        IosViewModelHolder.preloadBitmaps()
    }

    return ComposeUIViewController {
        App()
    }
}
