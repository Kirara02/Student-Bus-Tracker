package com.uniguard.bustracker.core.di.usecase

import com.uniguard.bustracker.core.domain.repository.UserRepository
import com.uniguard.bustracker.core.domain.usecase.GetUserByUidUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserUseCaseModule {

    @Provides
    @Singleton
    fun provideGetUserByUidUseCase(userRepository: UserRepository): GetUserByUidUseCase {
        return GetUserByUidUseCase(userRepository)
    }

}