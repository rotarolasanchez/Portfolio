package com.rotarola.portafolio_kotlin.di

import com.example.feature_login.domain.repositories.UserRepository
import com.example.feature_login.domain.repositories.UserRepositoryImpl
import com.rotarola.data.repository.UserDBRepository
import com.rotarola.data.util.database.RealmDBService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
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


    companion object {
        @Provides
        @Singleton
        fun provideRealmDBService(): RealmDBService = RealmDBService()

        @Provides
        @Singleton
        fun provideUserDBRepository(realmDBService: RealmDBService): UserDBRepository {
            return UserDBRepository(realmDBService)
        }
    }
}
