package com.rotarola.portafolio_kotlin.di
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rotarola.portafolio_kotlin.data.datasources.AuthDataSource
import com.rotarola.portafolio_kotlin.data.repository.AuthRepositoryImpl
import com.rotarola.portafolio_kotlin.domain.repositories.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthDataSource(
    ): AuthDataSource = AuthDataSource()

    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: AuthDataSource
    ): AuthRepository = AuthRepositoryImpl(authDataSource)
}