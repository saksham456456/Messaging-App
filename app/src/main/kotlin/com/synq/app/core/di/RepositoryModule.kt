package com.synq.app.core.di
import com.synq.app.data.repository.AuthRepositoryImpl
import com.synq.app.data.repository.ChatRepositoryImpl
import com.synq.app.domain.repository.AuthRepository
import com.synq.app.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class) abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
