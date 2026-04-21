package di

import core.storage.CredentialsStorage
import domain.usecases.*
import org.koin.dsl.module
import presentation.viewmodels.AuthViewModel
import presentation.viewmodels.ChatBotViewModel
import presentation.viewmodels.MenuViewModel

val commonModule = module {
    // Domain Layer - Use Cases
    single { AnalyzeImageUseCase(get()) }
    single { SolveProblemUseCase(get()) }
    single { ContinueChatUseCase(get()) }
    single { QueryFacturasUseCase(get()) }  // ← AGREGAR
    single { SignInWithEmailUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { QueryOllamaUseCase(get()) }  // ← NUEVO

    // Presentation Layer - ViewModels
    factory {
        ChatBotViewModel(
            analyzeImageUseCase   = get<AnalyzeImageUseCase>(),
            solveProblemUseCase   = get<SolveProblemUseCase>(),
            continueChatUseCase   = get<ContinueChatUseCase>(),
            queryFacturasUseCase  = get(),
            queryOllamaUseCase    = get()  // ← NUEVO
        )
    }

    factory {
        AuthViewModel(
            signWithEmailUseCase = get<SignInWithEmailUseCase>(),
            credentialsStorage = get<CredentialsStorage>(),
            logoutUseCase = get<LogoutUseCase>()
        )
    }
    factory { MenuViewModel() }
}


