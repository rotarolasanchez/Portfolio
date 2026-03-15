import androidx.compose.runtime.Composable
import presentation.view.atoms.theme.FeatureUITheme
import presentation.view.pages.NavigationMain

@Composable
fun App() {
    FeatureUITheme {
        NavigationMain()
    }
}