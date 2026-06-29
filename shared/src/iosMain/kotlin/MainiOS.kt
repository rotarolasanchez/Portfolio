import androidx.compose.ui.window.ComposeUIViewController
import di.IosViewModelHolder
import di.commonModule
import di.iosModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

@Suppress("unused") // llamada desde Swift: MainiOSKt.MainViewController()
fun MainViewController(): UIViewController {
    if (!IosViewModelHolder.isInitialized()) {
        // La versión ya está inyectada en compile time via SharedBuildConfig.APP_VERSION_NAME
        // NO llamar AppInfo.initialize() aquí — el Info.plist de Xcode tiene "1.0" y sobreescribiría "3.3.0"

        val koin = startKoin {
            modules(commonModule, iosModule)
        }.koin

        IosViewModelHolder.authViewModel    = koin.get()
        IosViewModelHolder.chatBotViewModel = koin.get()
        IosViewModelHolder.menuViewModel    = koin.get()

        IosViewModelHolder.preloadBitmaps()
    }

    return ComposeUIViewController {
        App()
    }
}
