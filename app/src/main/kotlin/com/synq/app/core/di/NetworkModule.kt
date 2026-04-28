package com.synq.app.core.di
import com.synq.app.data.remote.api.AuthApi
import com.synq.app.data.remote.api.ChatApi
import com.synq.app.data.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module @InstallIn(SingletonComponent::class) object NetworkModule {
    @Provides @Singleton fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)
    @Provides @Singleton fun provideChatApi(retrofit: Retrofit): ChatApi = retrofit.create(ChatApi::class.java)
    @Provides @Singleton fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)
}
