package com.rotarola.portafolio_kotlin.presentation.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.core.utils.TextRecognitionAnalyzer
import com.rotarola.portafolio_kotlin.domain.model.ChatMessage
import com.rotarola.portafolio_kotlin.domain.usecases.AnalyzeImageUseCase
import com.rotarola.portafolio_kotlin.domain.usecases.ContinueChatUseCase
import com.rotarola.portafolio_kotlin.domain.usecases.SolveProblemUseCase
import com.rotarola.portafolio_kotlin.presentation.state.ChatBotUiState
import com.rotarola.portafolio_kotlin.presentation.state.ScanState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val analyzeImageUseCase: AnalyzeImageUseCase,
    private val solveProblemUseCase: SolveProblemUseCase,
    private val continueChatUseCase: ContinueChatUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatBotUiState())
    val uiState: StateFlow<ChatBotUiState> = _uiState.asStateFlow()

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState

    fun reset() {
        _scanState.value = ScanState.Initial
    }

    fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessing = true,
                showCamera = false
            )
            try {
                val text = analyzeImageUseCase(bitmap)
                if (text.isNotBlank()) {
                    solveProblem(text)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = "No se detectó texto"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message
                )
            }
        }
    }

    private suspend fun solveProblem(problem: String) {
        try {
            val userMessage = ChatMessage(problem, true)
            val currentMessages = _uiState.value.messages + userMessage
            _uiState.value = _uiState.value.copy(messages = currentMessages)

            val response = solveProblemUseCase(problem)
            val aiMessage = ChatMessage(response, false)

            _uiState.value = _uiState.value.copy(
                messages = currentMessages + aiMessage,
                isProcessing = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                error = e.message
            )
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            val userMessage = ChatMessage(message, true)
            val currentMessages = _uiState.value.messages + userMessage
            _uiState.value = _uiState.value.copy(
                messages = currentMessages,
                isProcessing = true
            )

            try {
                val response = continueChatUseCase(currentMessages.dropLast(1), message)
                val aiMessage = ChatMessage(response, false)

                _uiState.value = _uiState.value.copy(
                    messages = currentMessages + aiMessage,
                    isProcessing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = e.message
                )
            }
        }
    }

    fun showCamera() {
        _uiState.value = _uiState.value.copy(showCamera = true)
    }

    fun hideCamera() {
        _uiState.value = _uiState.value.copy(showCamera = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
