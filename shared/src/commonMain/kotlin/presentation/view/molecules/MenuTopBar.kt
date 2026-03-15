package presentation.view.molecules

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.view.atoms.MenuIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopBar(
    onMenuClick: () -> Unit,
    tittle: String,
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}

