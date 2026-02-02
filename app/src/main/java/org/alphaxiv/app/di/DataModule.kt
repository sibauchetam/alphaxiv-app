package org.alphaxiv.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.alphaxiv.app.data.remote.AlphaXivApi
import org.alphaxiv.app.data.remote.NetworkPaperService
import org.alphaxiv.app.data.remote.PaperService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAlphaXivApi(okHttpClient: OkHttpClient): AlphaXivApi {
        return Retrofit.Builder()
            .baseUrl("https://api.alphaxiv.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlphaXivApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaperService(api: AlphaXivApi): PaperService {
        return NetworkPaperService(api)
    }
}
