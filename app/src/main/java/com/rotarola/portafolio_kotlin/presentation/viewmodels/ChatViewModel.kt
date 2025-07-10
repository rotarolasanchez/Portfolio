package com.rotarola.portafolio_kotlin.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.core.utils.GeminiService
import com.rotarola.portafolio_kotlin.presentation.view.pages.ChatMessage
import kotlinx.coroutines.launch
import javax.inject.Inject


// 5. Opcional: Crea un ViewModel para manejar la lógica de chat
class ChatViewModel @Inject constructor() : ViewModel() {
    private val geminiService = GeminiService()

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun initializeChat(initialProblem: String) {
        _messages.clear()
        _messages.add(ChatMessage(initialProblem, true))

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = geminiService.solveProblem(initialProblem)
                _messages.add(ChatMessage(response, false))
            } catch (e: Exception) {
                _messages.add(ChatMessage("Error al procesar el problema: ${e.message}", false))
            }
            _isLoading.value = false
        }
    }

    fun sendMessage(message: String) {
        _messages.add(ChatMessage(message, true))

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = geminiService.continueChatConversation(
                    _messages.dropLast(1),
                    message
                )
                _messages.add(ChatMessage(response, false))
            } catch (e: Exception) {
                _messages.add(ChatMessage("Error: ${e.message}", false))
            }
            _isLoading.value = false
        }
    }
}