package com.rotarola.portafolio_kotlin.presentation.view.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CapibaraBeigeLight,
    secondary = CapibaraGreyLight,
    tertiary = CapibaraWhiteDark
)

private val LightColorScheme = lightColorScheme(
    primary = CapibaraBrownMedium,
    secondary = CapibaraGreyLight,
    tertiary = CapibaraWhiteDark,
    background = CapibaraBeigeLight,
    primaryContainer = GreenSuccess40,
    onPrimaryContainer = GreenSuccess40,
    onPrimary = Color.White,
    errorContainer = RedError40,
    onErrorContainer = RedError40,
    onSecondary = CapibaraGreyLight
)

@Composable
fun FeatureUITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val typography = Typography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}