package com.gregspitz.flashcardappkotlin.data.source

import android.app.Application
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardLocalDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Dagger module for Repository
 */
@Module
class RepoModule {

    @Provides @Singleton
    fun provideLocalDataSource() : FakeFlashcardLocalDataSource {
        return FakeFlashcardLocalDataSource()
    }

    @Provides @Singleton
    fun provideRepository(flashcardLocalDataSource: FakeFlashcardLocalDataSource)
            : FlashcardRepository {
        return FlashcardRepository(flashcardLocalDataSource)
    }
}
