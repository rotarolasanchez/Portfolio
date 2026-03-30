package presentation.view.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource

/**
 * Helper to load drawable resources in a platform-safe way.
 * [resourcePath] should point to the underlying file in composeResources (e.g. "drawable/logo.png").
 */
@Composable
expect fun safeImagePainter(resource: DrawableResource, resourcePath: String): Painter
