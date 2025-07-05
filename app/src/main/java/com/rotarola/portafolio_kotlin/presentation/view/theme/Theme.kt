package com.rotarola.feature_ui.presentation.view.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CapibaraBeigeLight,
    secondary = CapibaraGreyLight,
    tertiary = CapibaraWhiteDark
)

private val LightColorScheme = lightColorScheme(
    primary = CapibaraBrownMedium,
    secondary = CapibaraGreyLight,
    tertiary = CapibaraWhiteDark,
    // Other default colors to override
    background = CapibaraBeigeLight,
    primaryContainer = GreenSuccess40,
    onPrimaryContainer = GreenSuccess40,
    onPrimary = Color.White,
    errorContainer = RedError40,
    onErrorContainer = RedError40,
    onSecondary = CapibaraGreyLight
    //surface = Color(0xFFFFFBFE),
    //onPrimary = Color.White,
    //onSecondary = Color.White,
    //onTertiary = Color.White,
    //onBackground = Color(0xFF1C1B1F),
    //onSurface = Color(0xFF1C1B1F),
)

@Composable
fun Feature_UITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    /*val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }*/
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