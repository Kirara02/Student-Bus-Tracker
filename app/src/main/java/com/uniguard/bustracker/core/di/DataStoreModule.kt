package com.uniguard.bustracker.core.di

import android.app.Application
import com.uniguard.bustracker.core.data.datasource.local.SettingDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideSettingDataStore(application: Application): SettingDataStore {
        return SettingDataStore(application)
    }

}