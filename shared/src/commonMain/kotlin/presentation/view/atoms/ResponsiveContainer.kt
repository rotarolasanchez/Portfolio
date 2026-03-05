package presentation.view.atoms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Container responsivo que adapta el layout según el tamaño de pantalla
 */
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    maxWidth: Dp = 600.dp,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    enableScroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    if (enableScroll) {
        val scrollState = rememberScrollState()

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
                content = content
            )
        }
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
                content = content
            )
        }
    }
}

/**
 * Espaciado responsivo que se adapta al tamaño de pantalla
 */
@Composable
fun ResponsiveSpacer(
    modifier: Modifier = Modifier,
    small: Dp = 8.dp,
    medium: Dp = 16.dp,
    large: Dp = 24.dp
) {
    // Por simplicidad, usamos tamaño medium
    // En una implementación completa se evaluaría el tamaño de pantalla
    Spacer(modifier = modifier.height(medium))
}
