package presentation.view.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import portafolio_kotlin.shared.generated.resources.Res
import presentation.state.toCameraUiState
import presentation.utils.getDrawerWidth
import presentation.view.molecules.MenuTopBar
import presentation.view.organisms.CameraScreen
import presentation.view.organisms.ChatScreen
import presentation.view.organisms.MenuDrawerContent
import core.model.PlatformBitmap
import presentation.state.ChatMode
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel
import kotlin.collections.emptyMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotTemplate(
    viewModel: ChatBotViewModel,
    menuViewModel: MenuViewModel,
    onNavigateToSection: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val menuUiState by menuViewModel.uiState.collectAsState()

    val modes = listOf(
        Triple(ChatMode.LIBRE,  "💬", "Libre"),
        Triple(ChatMode.AGENTE, "🏢", "Agente"),
        Triple(ChatMode.OLLAMA, "🦙", "Ollama"),
    )

    // Título dinámico según el modo activo
    val topBarTitle = when (uiState.chatMode) {
        ChatMode.LIBRE   -> "Chat Bot 💬"
        ChatMode.AGENTE  -> "Agente Comercial 🏢"
        ChatMode.OLLAMA  -> "Agente Ollama 🦙"
    }


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
            /*topBar = {
                MenuTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    tittle = "Chat Bot",
                    onLogout = onLogout
                )
            }*/
            topBar = {
                Column {
                    MenuTopBar(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        tittle = topBarTitle,
                        onLogout = onLogout
                    )
                    // ✅ SegmentedButton de 3 modos
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SingleChoiceSegmentedButtonRow {
                            modes.forEachIndexed { index, (mode, icon, label) ->
                                SegmentedButton(
                                    selected = uiState.chatMode == mode,
                                    onClick = {
                                        // Cicla al modo correcto directamente
                                        if (uiState.chatMode != mode) {
                                            viewModel.setMode(mode)
                                        }
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = modes.size
                                    ),
                                    label = {
                                        Text(
                                            text = "$icon $label",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
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
}