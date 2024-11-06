package com.rotarola.feature_ui.presentation.atoms

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun HeaderImage(modifier: Modifier, type:String) {
    var spec:LottieCompositionSpec= LottieCompositionSpec.Url ("")
    when(type) {
        "Geolocation" -> spec = LottieCompositionSpec.Url ("https://assets8.lottiefiles.com/packages/lf20_svy4ivvy.json")
        "Question" -> spec = LottieCompositionSpec.Url ("https://assets2.lottiefiles.com/packages/lf20_LIU4vHuu1W.json")
        "Login" ->
            spec = LottieCompositionSpec.Url ("https://lottie.host/8a9210cd-1f98-4870-a54f-7174b48f74ad/uJnUPZUy8M.json")
    }
    val composition by rememberLottieComposition(spec)

    // Estado que controla el progreso de la animación
    var isAnimating by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            // Resetear el progreso cuando comience la animación
            progress = 0f
            while (progress < 1f) {
                progress += 0.02f // Ajusta este valor según la velocidad deseada
                delay(16) // Aproximadamente 60 FPS
            }
            // Detener la animación al llegar al final
            isAnimating = false
        }
    }

    // Hacer clic para iniciar la animación nuevamente
    Box(modifier = modifier.clickable {
        isAnimating = true // Reiniciar la animación al hacer clic
    }) {
        LottieAnimation(
            composition = composition,
            progress = { progress }, // Controla el progreso manualmente
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AnimatedLiquidBackground() {
    var waveHeight by remember { mutableStateOf(0f) }
    val animatedWaveHeight by animateFloatAsState(
        targetValue = waveHeight,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        waveHeight = 100f // Ajusta el valor de la altura de la onda
    }
    val colorSecondary = MaterialTheme.colorScheme.secondary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val path = Path().apply {
                moveTo(0f, height * 0.5f + animatedWaveHeight)
                cubicTo(
                    width * 0.25f, height * 0.4f + animatedWaveHeight,
                    width * 0.75f, height * 0.6f + animatedWaveHeight,
                    width, height * 0.5f + animatedWaveHeight
                )
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(path, color = colorSecondary)
        }
    }
}