package com.example.data_store.di

import android.content.Context
import com.example.data_store.AppBlockDataStore
import com.example.data_store.AuthDataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): AuthDataStoreRepository {
        return AuthDataStoreRepository(context)
    }

    @Provides
    @Singleton
    fun provideAppBlockDataStore(@ApplicationContext context: Context): AppBlockDataStore {
        return AppBlockDataStore(context)
    }
}