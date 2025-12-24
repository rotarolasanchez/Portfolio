import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.rotarola.portafolio_kotlin.presentation.view.atoms.MenuFab
import com.rotarola.portafolio_kotlin.presentation.view.molecules.MenuTopBar
import com.rotarola.portafolio_kotlin.presentation.view.organisms.MenuDrawerContent
import com.rotarola.portafolio_kotlin.presentation.viewmodels.MenuViewModel
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTemplate(
    viewModel: MenuViewModel,
    onNavigateToSection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerContentColor = MaterialTheme.colorScheme.onBackground,
            ) {
                MenuDrawerContent(
                    items = uiState.menuItems,
                    icons = uiState.menuIcons,
                    selectedItem = uiState.selectedItem,
                    onItemSelected = {
                        viewModel.selectMenuItem(it)
                        onNavigateToSection(it)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    onMenuClick = { scope.launch { drawerState.open() }}
                    , tittle =  "Menu"
                )
            },
            floatingActionButton = {
                MenuFab(onClick = viewModel::onFabClick)
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Contenido Principal",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

