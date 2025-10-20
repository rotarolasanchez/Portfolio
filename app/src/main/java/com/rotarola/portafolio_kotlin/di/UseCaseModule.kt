package com.rotarola.portafolio_kotlin.di

import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import com.rotarola.portafolio_kotlin.domain.usecases.AnalyzeImageUseCase
import com.rotarola.portafolio_kotlin.domain.usecases.ContinueChatUseCase
import com.rotarola.portafolio_kotlin.domain.usecases.SolveProblemUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAnalyzeImageUseCase(
        chatRepository: ChatRepository
    ): AnalyzeImageUseCase = AnalyzeImageUseCase(chatRepository)

    @Provides
    @Singleton
    fun provideSolveProblemUseCase(
        chatRepository: ChatRepository
    ): SolveProblemUseCase = SolveProblemUseCase(chatRepository)

    @Provides
    @Singleton
    fun provideContinueChatUseCase(
        chatRepository: ChatRepository
    ): ContinueChatUseCase = ContinueChatUseCase(chatRepository)
}