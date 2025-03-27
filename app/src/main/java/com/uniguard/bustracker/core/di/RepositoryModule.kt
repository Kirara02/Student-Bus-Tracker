package com.uniguard.bustracker.core.di

import com.google.gson.GsonBuilder
import com.uniguard.bustracker.BuildConfig
import com.uniguard.bustracker.core.data.datasource.remote.APIService
import com.uniguard.bustracker.core.data.repository.UserRepositoryImpl
import com.uniguard.bustracker.core.domain.repository.UserRepository
import com.uniguard.bustracker.core.network.interceptor.HttpRequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(apiService: APIService): UserRepository {
        return UserRepositoryImpl(apiService)
    }

}