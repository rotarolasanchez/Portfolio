package presentation.view.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun safeIconPainter(resource: DrawableResource): Painter = painterResource(resource)

