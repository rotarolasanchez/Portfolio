package di

import core.service.GeminiCloudService
import core.service.GeminiCloudServiceImpl
import core.service.TextRecognitionService
import core.service.TextRecognitionServiceImpl
import data.repository.IosChatBotRepositoryImpl
import data.repository.IosAuthRepositoryImpl
import domain.repositories.AuthRepository
import domain.repositories.ChatBotRepository
import org.koin.dsl.module

/**
 * Módulo Koin específico para iOS.
 *
 * Provee:
 * - Servicios nativos iOS (TextRecognitionService con Vision framework)
 * - GeminiCloudServiceImpl usando NSURLSession
 * - Repositorios iOS (Auth stub / ChatBot)
 *
 * Uso: llamar startKoin { modules(commonModule, iosModule) }
 * desde MainiOS.kt → MainViewController → AppDelegate Swift
 */
val iosModule = module {
    // Services
    single<TextRecognitionService> { TextRecognitionServiceImpl() }
    single<GeminiCloudService> { GeminiCloudServiceImpl() }

    // Repositories
    single<AuthRepository> { IosAuthRepositoryImpl() }
    single<ChatBotRepository> { IosChatBotRepositoryImpl(get(), get()) }
}

