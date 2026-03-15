import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import org.koin.core.context.startKoin
import di.webCommonModule

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(webCommonModule)
    }

    CanvasBasedWindow(
        canvasElementId = "ComposeTarget",
        title = "Portafolio Kotlin - ChatBot AI"
    ) {
        App()
    }
}

