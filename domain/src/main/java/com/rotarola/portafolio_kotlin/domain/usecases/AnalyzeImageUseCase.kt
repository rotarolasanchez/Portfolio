package com.rotarola.portafolio_kotlin.domain.usecases

import android.graphics.Bitmap
import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import javax.inject.Inject

class AnalyzeImageUseCase @Inject constructor(
    private val chatRepository: ChatRepository // Inyecta INTERFAZ
) {
    suspend operator fun invoke(bitmap: Bitmap): String {
        return chatRepository.analyzeImage(bitmap)
    }
}