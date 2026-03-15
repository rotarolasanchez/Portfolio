package core.service


interface TextRecognitionService {
    suspend fun recognizeText(imageData: ByteArray): String
}