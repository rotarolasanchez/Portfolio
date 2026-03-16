package presentation.view.atoms.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// ──────────────────────────────────────────────
// Light Scheme — completo con todos los roles MD3
// ──────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    // Primario
    primary            = CapibaraBrownMedium,     // #8B4513  botones, checkbox, bordes activos
    onPrimary          = Color.White,              // texto sobre primary
    primaryContainer   = CapibaraBrownLight,       // #D2B48C  chips, badges, fondo de contenedor
    onPrimaryContainer = CapibaraBrownDark,        // #5C4033  texto sobre primaryContainer

    // Secundario
    secondary          = CapibaraBeigeDark,        // #8C6D56
    onSecondary        = Color.White,
    secondaryContainer = CapibaraBeigeLight,       // #FEE7D7
    onSecondaryContainer = CapibaraBrownDark,

    // Terciario
    tertiary           = CapibaraGreyDark,         // #6F675E
    onTertiary         = Color.White,
    tertiaryContainer  = CapibaraGreigeLight,      // #D8D2C1
    onTertiaryContainer = CapibaraBrownDark,

    // Error — MD3 exige que onErrorContainer sea legible SOBRE errorContainer
    error              = RedError40,               // rojo oscuro para texto/icono de error
    onError            = Color.White,
    errorContainer     = RedError80,               // rojo CLARO como fondo del container
    onErrorContainer   = Color(0xFF410002),        // casi negro-rojo, contraste sobre RedError80

    // Background & Surface
    background         = CapibaraBeigeLight,       // #FEE7D7
    onBackground       = CapibaraBrownDark,        // #5C4033 texto sobre background
    surface            = Color(0xFFFFF8F5),        // blanco cálido para cards, dialogs
    onSurface          = CapibaraBrownDark,
    surfaceVariant     = CapibaraGreigeLight,      // #D8D2C1
    onSurfaceVariant   = CapibaraGreigeDark,       // #7C7465

    // Contornos
    outline            = CapibaraBeigeDark,        // borde de TextField, Divider
    outlineVariant     = CapibaraGreigeLight,

    // Éxito (success) — MD3 no tiene rol oficial; se usa primaryContainer para ello
    // GreenSuccess40 y GreenSuccess80 quedan disponibles para uso manual
)

// ──────────────────────────────────────────────
// Dark Scheme — roles MD3 completos
// ──────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = CapibaraBrownLight,       // claro sobre fondo oscuro
    onPrimary          = CapibaraBrownDark,
    primaryContainer   = CapibaraBrownDark,
    onPrimaryContainer = CapibaraBeigeLight,

    secondary          = CapibaraGreigeLight,
    onSecondary        = CapibaraBrownDark,
    secondaryContainer = CapibaraGreigeDark,
    onSecondaryContainer = CapibaraBeigeLight,

    tertiary           = CapibaraGreyLight,
    onTertiary         = CapibaraBrownDark,
    tertiaryContainer  = CapibaraGreyDark,
    onTertiaryContainer = CapibaraBeigeLight,

    error              = RedError80,
    onError            = Color(0xFF690005),
    errorContainer     = Color(0xFF93000A),
    onErrorContainer   = RedError80,

    background         = Color(0xFF1C1410),
    onBackground       = CapibaraBeigeLight,
    surface            = Color(0xFF231E1A),
    onSurface          = CapibaraBeigeLight,
    surfaceVariant     = CapibaraGreigeDark,
    onSurfaceVariant   = CapibaraGreigeLight,

    outline            = CapibaraGreigeMedium,
    outlineVariant     = CapibaraGreigeDark,
)

// ──────────────────────────────────────────────
// Shapes MD3 — FilledButton requiere extraLarge (≈ CircleShape)
// ──────────────────────────────────────────────
private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)  // usado por Button, FAB en MD3
)

@Composable
fun FeatureUITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = AppShapes,
        content     = content
    )
}