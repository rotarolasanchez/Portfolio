package presentation.view.organisms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import presentation.view.atoms.safeIconPainter
import portafolio_kotlin.shared.generated.resources.Res
import portafolio_kotlin.shared.generated.resources.capibara_family_not_background
import presentation.view.atoms.safeImagePainter

@Composable
fun MenuDrawerContent(
    items: List<String>,
    icons: List<DrawableResource>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = safeImagePainter(
                    Res.drawable.capibara_family_not_background,
                    "drawable/capibara_family_not_background.png"
                ),
                contentDescription = "Logo"
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        items.forEachIndexed { index, item ->
            // Guarda defensiva: evitar IndexOutOfBoundsException si listas no coinciden
            val icon = icons.getOrNull(index) ?: return@forEachIndexed
            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = safeIconPainter(icon),
                        contentDescription = item,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                label = {
                    Text(
                    text = item,
                        color = MaterialTheme.colorScheme.primary
                )  },
                selected = item == selectedItem,
                onClick = { onItemSelected(item) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.background,
                    unselectedContainerColor = MaterialTheme.colorScheme.background,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}