package presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Utilidades para hacer los componentes responsivos
 */

/**
 * Obtiene el padding responsivo basado en el tamaño de pantalla
 */
@Composable
fun getResponsivePadding(): Dp {
    // En una implementación real, aquí usaríamos LocalConfiguration
    // Para simplificar, usamos valores fijos pero se puede expandir
    return 16.dp
}

/**
 * Obtiene el ancho máximo para contenido responsivo
 */
@Composable
fun getContentMaxWidth(): Dp {
    return 600.dp
}

/**
 * Obtiene el espaciado responsivo entre elementos
 */
@Composable
fun getResponsiveSpacing(): Dp {
    return 16.dp
}

/**
 * Obtiene el tamaño de icono responsivo
 */
@Composable
fun getResponsiveIconSize(): Dp {
    return 24.dp
}

/**
 * Obtiene el padding del drawer para diferentes pantallas
 */
@Composable
fun getDrawerWidth(): Dp {
    // En tablets/desktop, drawer más ancho
    return 280.dp
}
