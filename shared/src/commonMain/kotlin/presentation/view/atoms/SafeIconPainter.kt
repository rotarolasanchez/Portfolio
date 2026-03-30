package presentation.view.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource

/**
 * Carga un icono de forma segura.
 * En iOS devuelve imageVector equivalente (sin painterResource) para evitar
 * el crash _NSCFNumber de Trace.uikit.kt en CMP 1.8.0.
 * En otras plataformas usa painterResource directamente.
 */
@Composable
expect fun safeIconPainter(resource: DrawableResource): Painter

