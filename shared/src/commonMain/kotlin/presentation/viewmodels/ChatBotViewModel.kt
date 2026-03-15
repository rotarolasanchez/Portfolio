package presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.model.ChatBotMessage
import domain.usecases.AnalyzeImageUseCase
import domain.usecases.ContinueChatUseCase
import domain.usecases.SolveProblemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import presentation.state.ChatBotUiState
import presentation.state.ScanState
import presentation.view.organisms.PlatformBitmap

class ChatBotViewModel(
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

    fun processImage(bitmap: PlatformBitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isProcessing = true,
                showCamera = false
            )
            try {
                // Agregar mensaje del usuario indicando que adjuntó imagen
                val userMessage = ChatBotMessage("📎 Imagen adjuntada para análisis", isFromUser = true)
                _uiState.update { it.copy(messages = it.messages + userMessage) }

                // analyzeImage ya retorna el análisis completo (OCR+Gemini en Android, Gemini directo en Web)
                val analysisResult = analyzeImageUseCase(bitmap)
                if (analysisResult.isNotBlank()) {
                    val botMessage = ChatBotMessage(analysisResult, isFromUser = false)
                    _uiState.update {
                        it.copy(
                            messages = it.messages + botMessage,
                            isProcessing = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = "No se pudo analizar la imagen"
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
            val userMessage = ChatBotMessage(problem, true)
            val currentMessages = _uiState.value.messages + userMessage
            _uiState.value = _uiState.value.copy(messages = currentMessages)

            val response = solveProblemUseCase(problem)
            val aiMessage = ChatBotMessage(response, false)

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

    fun showCamera() {
        _uiState.value = _uiState.value.copy(showCamera = true)
    }

    fun hideCamera() {
        _uiState.value = _uiState.value.copy(showCamera = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val userMessage = ChatBotMessage(text, isFromUser = true)
                _uiState.update { it.copy(messages = it.messages + userMessage) }

                val response = continueChatUseCase(_uiState.value.messages, text)

                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatBotMessage(response, isFromUser = false),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                println("ChatBotViewModel: Error sending message - ${e.message}")
                _uiState.update {
                    it.copy(
                        messages = it.messages + ChatBotMessage(
                            "Error: ${e.message}",
                            isFromUser = false
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

}