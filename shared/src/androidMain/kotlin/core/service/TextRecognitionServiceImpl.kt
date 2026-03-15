package core.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TextRecognitionServiceImpl : TextRecognitionService {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognizeText(imageData: ByteArray): String = withContext(Dispatchers.IO) {
        Log.e("TextRecognizer", "Procesando imagen de ${imageData.size} bytes")
        try {
            // Convertir ByteArray a Bitmap
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                ?: throw IllegalArgumentException("No se pudo decodificar la imagen")

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