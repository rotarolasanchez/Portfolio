package com.rotarola.portafolio_kotlin.di

import com.rotarola.portafolio_kotlin.domain.repositories.UserRepository
import com.rotarola.portafolio_kotlin.domain.repositories.UserRepositoryImpl
import com.rotarola.portafolio_kotlin.data.repository.UserDBRepository
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
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
