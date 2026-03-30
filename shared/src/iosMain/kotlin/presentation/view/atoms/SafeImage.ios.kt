package presentation.view.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import di.IosViewModelHolder
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * iOS: Carga imágenes PNG desde los bitmaps precargados en IosViewModelHolder (ruta rápida).
 * Si el bitmap no está disponible (preloading falló), usa painterResource como fallback.
 * NOTA: painterResource es seguro para PNGs en CMP 1.8.0.
 *       El crash _NSCFNumber solo afecta a XML vector drawables (iconos), no a PNGs.
 */
@Composable
actual fun safeImagePainter(resource: DrawableResource, resourcePath: String): Painter {
    // Ruta rápida: bitmap precargado al inicio (sin I/O en composición)
    val bitmap = IosViewModelHolder.preloadedBitmaps[resourcePath]
    if (bitmap != null) return BitmapPainter(bitmap)

    // Fallback: painterResource estándar de CMP (funciona para PNG en CMP 1.8.0)
    return painterResource(resource)
}
