import domain.repositories.ChatBotRepository
import domain.usecases.ContinueChatUseCase

// Este archivo fue reemplazado por CommonModule.kt usando Koin
// para compatibilidad con Kotlin Multiplatform
fun provideContinueChatUseCase(
        chatRepository: ChatBotRepository
    ): ContinueChatUseCase = ContinueChatUseCase(chatRepository)
