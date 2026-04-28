package com.synq.app.core.di
import android.content.Context
import androidx.room.Room
import com.synq.app.data.local.SynqDatabase
import com.synq.app.data.local.dao.ChatDao
import com.synq.app.data.local.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class) object DatabaseModule {
    @Provides @Singleton fun provideSynqDatabase(@ApplicationContext context: Context): SynqDatabase = Room.databaseBuilder(context, SynqDatabase::class.java, "synq_database").fallbackToDestructiveMigration().build()
    @Provides fun provideChatDao(database: SynqDatabase): ChatDao = database.chatDao()
    @Provides fun provideMessageDao(database: SynqDatabase): MessageDao = database.messageDao()
}
