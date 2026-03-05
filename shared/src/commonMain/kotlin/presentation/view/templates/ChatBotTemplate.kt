package presentation.view.templates

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import portafolio_kotlin.shared.generated.resources.Res
import presentation.state.toCameraUiState
import presentation.utils.getDrawerWidth
import presentation.view.molecules.MenuTopBar
import presentation.view.organisms.CameraScreen
import presentation.view.organisms.ChatScreen
import presentation.view.organisms.MenuDrawerContent
import presentation.view.organisms.PlatformBitmap
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel
import kotlin.collections.emptyMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotTemplate(
    viewModel: ChatBotViewModel,
    menuViewModel: MenuViewModel,
    onNavigateToSection: (String) -> Unit = {}

) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val menuUiState by menuViewModel.uiState.collectAsState()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerContentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(getDrawerWidth())
            ) {
                MenuDrawerContent(
                    items = menuUiState.menuItems,
                    icons = menuUiState.menuIcons,
                    selectedItem = "Chat Bot",
                    onItemSelected = { item ->
                        onNavigateToSection(item)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    tittle = "Chat Bot"
                )
            }
        ) { innerPadding ->
            when {
                uiState.showCamera -> {
                    CameraScreen(
                        uiState = uiState.toCameraUiState(),
                        onImageCaptured = { bitmap: PlatformBitmap ->
                            viewModel.processImage(bitmap)
                        },
                        onRetakePhoto = { viewModel.hideCamera() }
                    )
                }

                else -> {
                    ChatScreen(
                        uiState = uiState,
                        onSendMessage = { message -> viewModel.sendMessage(message) },
                        onCameraClick = { viewModel.showCamera() }
                    )
                }
            }
        }
    }
}