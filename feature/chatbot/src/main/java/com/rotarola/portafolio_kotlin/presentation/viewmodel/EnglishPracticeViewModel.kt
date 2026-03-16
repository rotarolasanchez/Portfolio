package com.rotarola.portafolio_kotlin.presentation.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.core.service.GeminiCloudService
import com.rotarola.portafolio_kotlin.presentation.state.EnglishPracticeUiState
import com.rotarola.portafolio_kotlin.presentation.state.PracticeMode
import com.rotarola.portafolio_kotlin.presentation.state.VoiceMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EnglishPracticeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val geminiCloudService: GeminiCloudService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnglishPracticeUiState())
    val uiState: StateFlow<EnglishPracticeUiState> = _uiState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null

    init {
        initTextToSpeech()
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.US
                textToSpeech?.setSpeechRate(0.9f)
                textToSpeech?.setPitch(1.0f)
            }
        }
    }

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _uiState.update { it.copy(error = "Speech recognition not available on this device") }
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _uiState.update { it.copy(isListening = true, error = null) }
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _uiState.update { it.copy(isListening = false) }
            }
            override fun onError(error: Int) {
                _uiState.update { it.copy(isListening = false, error = "Could not understand, please try again") }
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: return
                onUserSpoke(text)
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                _uiState.update { it.copy(currentTranscription = partial) }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

    private fun onUserSpoke(text: String) {
        val userMessage = VoiceMessage(text = text, isFromUser = true)
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                currentTranscription = "",
                isProcessing = true
            )
        }
        sendToGemini(text)
    }

    private fun sendToGemini(userText: String) {
        viewModelScope.launch {
            try {
                val prompt = buildPrompt(userText)
                val response = geminiCloudService.sendMessage(prompt)
                val botMessage = VoiceMessage(text = response, isFromUser = false)
                _uiState.update {
                    it.copy(
                        messages = it.messages + botMessage,
                        isProcessing = false
                    )
                }
                speakOut(response)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = "Error connecting to AI: ${e.message}"
                    )
                }
            }
        }
    }

    private fun buildPrompt(userText: String): String {
        return when (_uiState.value.practiceMode) {
            PracticeMode.FREE_CONVERSATION ->
                """You are a friendly English tutor helping a Spanish-speaking Android developer practice conversational English.\n                The user said: \"$userText\"\n                Instructions:\n                - Respond naturally in English (2-3 sentences max)\n                - If there are grammar mistakes, gently correct them at the end\n                - Keep the conversation going with a follow-up question\n                - Be encouraging and positive\n                Format: [Your response] | Correction (if any): [correction]"""

            PracticeMode.TECHNICAL_INTERVIEW ->
                """You are a technical interviewer at a top tech company interviewing a Senior Android Developer.\n                The candidate said: \"$userText\"\n                Instructions:\n                - Respond as a professional interviewer in English\n                - Ask follow-up technical questions about Android, Kotlin, Jetpack Compose, Clean Architecture\n                - Give brief feedback on their answer\n                - Keep responses concise (2-3 sentences)"""

            PracticeMode.VOCABULARY_PRACTICE ->
                """You are an English vocabulary coach for a Senior Android Developer.\n                The user said: \"$userText\"\n                Instructions:\n                - Respond in English and highlight 1-2 technical vocabulary words they used well or could improve\n                - Suggest better professional alternatives if applicable\n                - Keep it conversational and encouraging"""
        }
    }

    private fun speakOut(text: String) {
        val cleanText = text.split("|").firstOrNull()?.trim() ?: text
        _uiState.update { it.copy(isSpeaking = true) }
        textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "utterance_id")
        textToSpeech?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                _uiState.update { it.copy(isSpeaking = false) }
            }
            override fun onError(utteranceId: String?) {
                _uiState.update { it.copy(isSpeaking = false) }
            }
        })
    }

    fun changePracticeMode(mode: PracticeMode) {
        _uiState.update { it.copy(practiceMode = mode, messages = emptyList()) }
        speakOut(
            when (mode) {
                PracticeMode.FREE_CONVERSATION -> "Let's have a conversation! Tell me about yourself."
                PracticeMode.TECHNICAL_INTERVIEW -> "Welcome to your technical interview. Please tell me about your Android development experience."
                PracticeMode.VOCABULARY_PRACTICE -> "Let's practice vocabulary! Tell me about a recent project you worked on."
            }
        )
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}