package com.rotarola.portafolio_kotlin.di

import com.rotarola.portafolio_kotlin.core.utils.GeminiService
import com.rotarola.portafolio_kotlin.core.utils.TextRecognitionAnalyzer
import com.rotarola.portafolio_kotlin.data.repository.ChatRepositoryImpl
import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatRepository(
        textAnalyzer: TextRecognitionAnalyzer,
        geminiService: GeminiService
    ): ChatRepository = ChatRepositoryImpl(textAnalyzer, geminiService)

    @Provides
    @Singleton
    fun provideTextRecognitionAnalyzer(): TextRecognitionAnalyzer {
        return TextRecognitionAnalyzer()
    }

    @Provides
    @Singleton
    fun provideGeminiService(): GeminiService {
        return GeminiService()
    }
}