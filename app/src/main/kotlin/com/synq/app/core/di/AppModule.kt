package com.synq.app.core.di
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.synq.app.core.network.AuthInterceptor
import com.synq.app.core.network.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class) object AppModule {
    @Provides @Singleton fun provideApplicationContext(@ApplicationContext context: Context): Context = context
    @Provides @Singleton fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    @Provides @Singleton fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    @Provides @Singleton fun provideTokenManager(@ApplicationContext context: Context): TokenManager = TokenManager(context)
    @Provides @Singleton fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor = AuthInterceptor(tokenManager)
    @Provides @Singleton fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply { level = if (com.synq.app.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE }
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).addInterceptor(authInterceptor).build()
    }
    @Provides @Singleton fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder().baseUrl("https://api.synq.app/").client(okHttpClient).addConverterFactory(MoshiConverterFactory.create(moshi)).build()
}
