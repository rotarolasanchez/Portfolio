package com.rotarola.portafolio_kotlin.presentation.viewmodels

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rotarola.portafolio_kotlin.core.utils.TextRecognitionAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
@HiltViewModel
class ScanViewModel @Inject constructor(
    private val textAnalyzer: TextRecognitionAnalyzer
) : ViewModel() {
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState = _scanState.asStateFlow()

    fun processImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            _scanState.value = ScanState.Processing
            try {
                val text = textAnalyzer.analyzeImage(imageProxy)
                _scanState.value = if (text.isNotEmpty()) {
                    ScanState.Success(text)
                } else {
                    ScanState.Error("No se encontró texto")
                }
            } catch (e: Exception) {
                _scanState.value = ScanState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}*/
@HiltViewModel
class ScanViewModel @Inject constructor(
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
            val text = analyzer.analyzeImage(bitmap)
            _scanState.value = if (text.isNotBlank()) {
                ScanState.Success(text)
            } else {
                ScanState.Error("No se reconoció texto.")
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