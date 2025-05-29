package com.example.di

import com.example.Urls
import com.example.api.AuthApi
import com.example.auth.JwtAuthInterceptor
import com.example.data_store.AuthDataStoreRepository
import com.example.network.api.ChatApi
import com.example.network.api.UserApi
import com.example.websocket.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideAuthOkHttpClient(jwtAuthInterceptor: JwtAuthInterceptor): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient().newBuilder()
            .addInterceptor(interceptor)
            .addInterceptor(jwtAuthInterceptor)
            .readTimeout(Duration.ofMinutes(1))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: AuthDataStoreRepository): JwtAuthInterceptor =
        JwtAuthInterceptor(tokenManager)


    @Provides
    @Singleton
    fun provideAuthApi(okHttpClient: OkHttpClient): AuthApi {
        return Retrofit.Builder()
            .baseUrl(Urls.AUTH_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(okHttpClient: OkHttpClient): UserApi {
        return Retrofit.Builder()
            .baseUrl(Urls.USERS_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApi(okHttpClient: OkHttpClient): ChatApi {
        return Retrofit.Builder()
            .baseUrl(Urls.CHAT_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWebSocketManager(
        okHttpClient: OkHttpClient
    ): WebSocketManager {
        return WebSocketManager(
            okHttpClient = okHttpClient,
            baseUrl = Urls.CHAT_API_URL
        )
    }
}
