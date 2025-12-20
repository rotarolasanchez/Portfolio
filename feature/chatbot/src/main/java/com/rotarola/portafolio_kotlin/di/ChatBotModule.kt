package com.rotarola.portafolio_kotlin.di

import com.rotarola.portafolio_kotlin.core.service.GeminiCloudService
import com.rotarola.portafolio_kotlin.core.service.TextRecognitionService
import com.rotarola.portafolio_kotlin.data.repository.ChatRepositoryImpl
import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatBotModule {
    @Provides
    @Singleton
    fun provideChatRepository(
        textAnalyzer: TextRecognitionService,
        geminiCloudService: GeminiCloudService // ✅ Cambiado de GeminiService
    ): ChatRepository = ChatRepositoryImpl(textAnalyzer, geminiCloudService)

    @Provides
    @Singleton
    fun provideTextRecognitionAnalyzer(): TextRecognitionService {
        return TextRecognitionService()
    }

    @Provides
    @Singleton
    fun provideGeminiCloudService(): GeminiCloudService {
        return GeminiCloudService()
    }
}