package di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import core.service.GeminiCloudService
import core.service.TextRecognitionService
import core.storage.AndroidCredentialsStorage
import core.storage.CredentialsStorage
import data.datasources.AuthDataSource
import data.repository.AuthRepositoryImpl
import data.repository.ChatBotRepositoryImpl
import domain.repositories.AuthRepository
import domain.repositories.ChatBotRepository
import org.koin.dsl.module

val androidModule = module {
    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Data Sources
    single { AuthDataSource() }

    // Services
    single<TextRecognitionService> { core.service.TextRecognitionServiceImpl() }
    single<GeminiCloudService> { core.service.GeminiCloudServiceImpl() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<ChatBotRepository> { ChatBotRepositoryImpl(get(), get()) }

    // Storage
    single<CredentialsStorage> { AndroidCredentialsStorage(get<Context>()) }
}
