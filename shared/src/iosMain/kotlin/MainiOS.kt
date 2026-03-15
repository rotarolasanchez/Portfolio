import androidx.compose.ui.window.ComposeUIViewController
import di.commonModule
import di.iosModule
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

/**
 * Punto de entrada de Compose Multiplatform para iOS.
 *
 * Este archivo expone MainViewController() al código Swift/Objective-C
 * del proyecto Xcode (iosApp/).
 *
 * Cómo usar desde Swift (AppDelegate.swift o @main ContentView.swift):
 *
 *   import shared
 *
 *   struct ContentView: View {
 *       var body: some View {
 *           ComposeView()
 *               .ignoresSafeArea(.keyboard) // Compose maneja el teclado
 *       }
 *   }
 *
 *   struct ComposeView: UIViewControllerRepresentable {
 *       func makeUIViewController(context: Context) -> UIViewController {
 *           MainViewControllerKt.MainViewController()
 *       }
 *       func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
 *   }
 *
 * Si usas UIKit puro (AppDelegate), en didFinishLaunchingWithOptions:
 *   let composeVC = MainViewControllerKt.MainViewController()
 *   window?.rootViewController = composeVC
 */

private var koinInitialized = false

fun MainViewController(): UIViewController {
    // Inicializar Koin solo una vez (protección ante hot reload)
    if (!koinInitialized) {
        startKoin {
            modules(commonModule, iosModule)
        }
        koinInitialized = true
    }

    return ComposeUIViewController {
        App()
    }
}

