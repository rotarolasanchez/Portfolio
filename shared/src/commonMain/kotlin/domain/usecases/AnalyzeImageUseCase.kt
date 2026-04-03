package domain.usecases

import core.model.PlatformBitmap
import domain.repositories.ChatBotRepository

class AnalyzeImageUseCase(
    private val repository: ChatBotRepository
) {
    suspend operator fun invoke(bitmap: PlatformBitmap): String {
        return repository.analyzeImage(bitmap)
    }
}

