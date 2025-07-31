package com.rotarola.portafolio_kotlin.di

import com.rotarola.portafolio_kotlin.domain.repositories.UserRepository
import com.rotarola.portafolio_kotlin.data.repository.UserRepositoryImpl
import com.rotarola.portafolio_kotlin.data.repository.UserDBRepository
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import com.rotarola.portafolio_kotlin.core.utils.GeminiService
import com.rotarola.portafolio_kotlin.core.utils.TextRecognitionAnalyzer
import com.rotarola.portafolio_kotlin.data.repository.ChatRepositoryImpl
import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    companion object {
        @Provides
        @Singleton
        fun provideRealmDBService(): RealmDBService = RealmDBService()

        @Provides
        @Singleton
        fun provideUserDBRepository(realmDBService: RealmDBService): UserDBRepository {
            return UserDBRepository(realmDBService)
        }

        @Provides
        @Singleton
        fun provideGeminiService(): GeminiService {
            return GeminiService()
        }

        @Provides
        @Singleton
        fun provideTextRecognitionAnalyzer(): TextRecognitionAnalyzer {
            return TextRecognitionAnalyzer()
        }
    }
}
