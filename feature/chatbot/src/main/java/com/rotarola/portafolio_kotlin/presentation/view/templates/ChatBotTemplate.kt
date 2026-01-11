package com.rotarola.portafolio_kotlin.presentation.view.templates

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.rotarola.portafolio_kotlin.presentation.state.toCameraUiState
import com.rotarola.portafolio_kotlin.presentation.view.molecules.MenuTopBar
import com.rotarola.portafolio_kotlin.presentation.view.organisms.CameraScreen
import com.rotarola.portafolio_kotlin.presentation.view.organisms.ChatScreen
import com.rotarola.portafolio_kotlin.presentation.viewmodels.ChatBotViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatBotTemplate(viewModel: ChatBotViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MenuTopBar(
                onMenuClick = { scope.launch { drawerState.open() } }
                , tittle= "Chat Bot"
            )
        }
    ) { innerPadding ->
        when {
            uiState.showCamera -> {
                CameraScreen(
                    uiState = uiState.toCameraUiState(),
                    onImageCaptured = viewModel::processImage,
                    onRetakePhoto = viewModel::hideCamera
                )
            }
            else -> {
                ChatScreen(
                    uiState = uiState,
                    onSendMessage = viewModel::sendMessage,
                    onCameraClick = viewModel::showCamera
                )
            }
        }
    }
}