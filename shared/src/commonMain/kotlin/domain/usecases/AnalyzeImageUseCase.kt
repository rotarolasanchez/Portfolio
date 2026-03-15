package domain.usecases

import domain.repositories.ChatBotRepository
import presentation.view.organisms.PlatformBitmap

class AnalyzeImageUseCase(
    private val repository: ChatBotRepository
) {
    suspend operator fun invoke(bitmap: PlatformBitmap): String {
        return repository.analyzeImage(bitmap)
    }
}

