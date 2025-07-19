package com.rotarola.portafolio_kotlin.presentation.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.core.utils.TextRecognitionAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatBotViewModel @Inject constructor(
    private val analyzer: TextRecognitionAnalyzer
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState

    fun reset() {
        _scanState.value = ScanState.Initial
    }

    fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _scanState.value = ScanState.Processing
            try {
                val text = analyzer.recognizeText(bitmap)
                Log.e("ScanViewModel", "Texto detectado-pre: $text")
                if (text.isNotBlank()) {
                    Log.e("ScanViewModel", "Texto detectado-post: $text")
                    _scanState.value = ScanState.Success(text)
                } else {
                    Log.e("ScanViewModel", "No se detectó texto en la imagen")
                    _scanState.value = ScanState.Error("No se detectó texto en la imagen")
                }
            } catch (e: Exception) {
                Log.e("ScanViewModel", "Error al procesar imagen: ${e.message}")
                _scanState.value = ScanState.Error("Error al procesar imagen: ${e.message}")
            }
        }
    }
}

sealed class ScanState {
    object Initial : ScanState()
    object Processing : ScanState()
    data class Success(val text: String) : ScanState()
    data class Error(val message: String) : ScanState()
}