package org.alphaxiv.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.alphaxiv.app.data.remote.MockPaperService
import org.alphaxiv.app.data.remote.PaperService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePaperService(): PaperService {
        return ScraperPaperService()
    }
}
