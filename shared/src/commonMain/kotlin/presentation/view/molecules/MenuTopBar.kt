package presentation.view.molecules

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.baseline_logout_24
import presentation.view.atoms.MenuIconButton
import presentation.view.atoms.safeIconPainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopBar(
    onMenuClick: () -> Unit,
    tittle: String,
    onLogout: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = tittle,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            MenuIconButton(
                contentDescription = "Menu",
                onClick = onMenuClick
            )
        },
        actions = {
            if (onLogout != null) {
                IconButton(onClick = onLogout) {
                    Icon(
                        painter = safeIconPainter(Res.drawable.baseline_logout_24),
                        contentDescription = "Cerrar sesión",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}

