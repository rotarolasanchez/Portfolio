package com.rotarola.portafolio_kotlin.core.utils

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*
class TextRecognitionAnalyzer @Inject constructor() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    suspend fun analyzeImage(imageProxy: ImageProxy): String {
        return withContext(Dispatchers.Default) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                try {
                    val result = recognizer.process(image).await()
                    result.text
                } catch (e: Exception) {
                    Log.e("TextRecognition", "Error: ${e.message}")
                    ""
                } finally {
                    imageProxy.close()
                }
            } else {
                ""
            }
        }
    }
}*/
class TextRecognitionAnalyzer @Inject constructor() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun analyzeImage(bitmap: Bitmap): String {
        return withContext(Dispatchers.Default) {
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = recognizer.process(image).await()
                result.text
            } catch (e: Exception) {
                Log.e("TextRecognition", "Error: ${e.message}")
                ""
            }
        }
    }
}