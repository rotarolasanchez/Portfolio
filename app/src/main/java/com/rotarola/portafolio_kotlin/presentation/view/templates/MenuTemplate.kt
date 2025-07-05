import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rotarola.portafolio_kotlin.R
import com.rotarola.portafolio_kotlin.presentation.view.atoms.MenuFab
import com.rotarola.portafolio_kotlin.presentation.view.moleculs.MenuTopBar
import com.rotarola.portafolio_kotlin.presentation.view.organisms.MenuDrawerContent
import com.rotarola.portafolio_kotlin.presentation.viewmodels.MenuViewModel
import kotlinx.coroutines.launch


/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTemplate() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val items = listOf("Home", "Profile", "Settings", "Help")
    val icons = listOf(
        painterResource(id = R.drawable.baseline_home_24),
        painterResource(id = R.drawable.baseline_person_24),
        painterResource(id = R.drawable.baseline_settings_24),
        painterResource(id = R.drawable.baseline_help_24)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxHeight(),
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerContentColor = MaterialTheme.colorScheme.primary
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.capibara_family),
                            contentDescription = "My image"
                        )
                    }

                    // Divider opcional entre header y opciones
                    Divider(color = Color.Gray, thickness = 1.dp)

                    // Drawer Items
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            label = { Text(text = item) },
                            selected = false,
                            onClick = {
                                // Acción cuando se selecciona un item
                            },
                            icon = {
                                Image(
                                    painter = icons[index],
                                    contentDescription = item,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                )
                            },
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                           ,colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedContainerColor = MaterialTheme.colorScheme.background,
                                unselectedTextColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Menu Page") },
                        navigationIcon = {
                            IconButton(onClick = {
                                // Abrir el drawer cuando se presiona el ícono del menú
                                coroutineScope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_home_24),
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.background
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            // Acción del FAB
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.background
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                }
            ) { innerPadding ->
                // Contenido de la página
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Text("Contenido Principal", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    )
}*/

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
                    onMenuClick = { scope.launch { drawerState.open() } }
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

