

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feature_menu.R
import kotlinx.coroutines.launch

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
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Drawer Header (opcional)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp)
                    ) {
                        // Puedes incluir un logotipo o información de usuario aquí
                        BasicText(text = "App Title", style = MaterialTheme.typography.headlineSmall)
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
                                    contentDescription = item
                                )
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
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
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            // Acción del FAB
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_add_24),
                            contentDescription = "Add"
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
}