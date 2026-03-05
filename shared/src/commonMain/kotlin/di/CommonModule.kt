package di

import domain.repositories.AuthRepository
import domain.repositories.ChatBotRepository
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
    single { SignInWithEmailUseCase(get()) }

    // Presentation Layer - ViewModels
    factory {
        ChatBotViewModel(
            analyzeImageUseCase = get<AnalyzeImageUseCase>(),
            solveProblemUseCase = get<SolveProblemUseCase>(),
            continueChatUseCase = get<ContinueChatUseCase>()
        )
    }
    factory {
        AuthViewModel(
            signWithEmailUseCase = get<SignInWithEmailUseCase>()
        )
    }
    factory { MenuViewModel() }
}


