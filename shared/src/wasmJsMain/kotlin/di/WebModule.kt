package di

import core.service.*
import data.repository.WebAuthRepositoryImpl
import domain.repositories.ChatBotRepository
import domain.usecases.*
import org.koin.dsl.module
import presentation.viewmodels.AuthViewModel
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel

/**
 * Módulo de inyección de dependencias específico para Web/WASM
 * Usa Firebase Auth real y Cloud Function de Gemini
 */
val webModule = module {
    // Services
    single<TextRecognitionService> { TextRecognitionServiceImpl() }
    single<GeminiCloudService> { GeminiCloudServiceImpl() }

    // Repositories
    single<ChatBotRepository> { WebChatBotRepositoryImpl(get(), get()) }

    // ✅ Auth Repository real con Firebase Auth JS SDK
    single<domain.repositories.AuthRepository> { WebAuthRepositoryImpl() }

    // Use Cases
    single { AnalyzeImageUseCase(get()) }
    single { SolveProblemUseCase(get()) }
    single { ContinueChatUseCase(get()) }
    single { SignInWithEmailUseCase(get()) }

    // ViewModels
    single { ChatBotViewModel(get(), get(), get()) }
    single { MenuViewModel() }
    single { AuthViewModel(get()) }
}

/**
 * Módulo común que incluye todas las dependencias para Web
 */
val webCommonModule = module {
    includes(webModule)
}
