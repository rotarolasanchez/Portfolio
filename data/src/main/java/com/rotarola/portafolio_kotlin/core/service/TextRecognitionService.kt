package com.rotarola.portafolio_kotlin.core.service

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TextRecognitionService @Inject constructor() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun recognizeText(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        Log.e("TextRecognizer", "Ingreso a recognizeText con bitmap: $bitmap")
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            Log.e("TextRecognizer", "Texto reconocido: ${result.text}")
            result.text
        } catch (e: Exception) {
            Log.e("TextRecognizer", "Error recognizing text", e)
            throw e
        }
    }
}