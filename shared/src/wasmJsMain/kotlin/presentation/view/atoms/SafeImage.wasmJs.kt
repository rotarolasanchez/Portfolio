package presentation.view.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun safeImagePainter(resource: DrawableResource, resourcePath: String): Painter {
    return painterResource(resource)
}
