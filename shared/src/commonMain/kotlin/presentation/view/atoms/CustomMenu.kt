package presentation.view.atoms

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.baseline_menu_24
import portafolio_kotlin.shared.generated.resources.baseline_add_24

@Composable
fun MenuIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(Res.drawable.baseline_menu_24),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun MenuFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(Res.drawable.baseline_add_24),
            contentDescription = "Add"
        )
    }
}