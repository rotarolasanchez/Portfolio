package presentation.view.atoms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.compose.resources.DrawableResource
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.baseline_account_circle_24
import portafolio_kotlin.shared.generated.resources.baseline_add_24
import portafolio_kotlin.shared.generated.resources.baseline_home_24
import portafolio_kotlin.shared.generated.resources.baseline_lock_24
import portafolio_kotlin.shared.generated.resources.baseline_menu_24
import portafolio_kotlin.shared.generated.resources.baseline_visibility_24
import portafolio_kotlin.shared.generated.resources.outline_login_24
import portafolio_kotlin.shared.generated.resources.outline_photo_camera_24
import portafolio_kotlin.shared.generated.resources.outline_robot_2_24
import portafolio_kotlin.shared.generated.resources.outline_send_24

/**
 * iOS: NO usa painterResource para evitar el crash _NSCFNumber de Trace.uikit.kt.
 * Mapea cada DrawableResource a su Material Icon equivalente.
 * Las comparaciones usan igualdad de referencia (los recursos son singletons lazy).
 */
@Composable
actual fun safeIconPainter(resource: DrawableResource): Painter {
    val icon = when (resource) {
        Res.drawable.baseline_account_circle_24 -> Icons.Filled.AccountCircle
        Res.drawable.baseline_lock_24           -> Icons.Filled.Lock
        Res.drawable.outline_login_24           -> Icons.AutoMirrored.Filled.Login
        Res.drawable.baseline_visibility_24     -> Icons.Filled.Visibility
        Res.drawable.baseline_home_24           -> Icons.Filled.Home
        Res.drawable.baseline_menu_24           -> Icons.Filled.Menu
        Res.drawable.baseline_add_24            -> Icons.Filled.Add
        Res.drawable.outline_send_24            -> Icons.AutoMirrored.Filled.Send
        Res.drawable.outline_photo_camera_24    -> Icons.Filled.PhotoCamera
        Res.drawable.outline_robot_2_24         -> Icons.Filled.Android
        else                                    -> return ColorPainter(Color.Transparent)
    }
    return rememberVectorPainter(icon)
}

