package com.gregspitz.flashcardappkotlin.di

import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.UseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCaseThreadPoolScheduler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import dagger.Module
import dagger.Provides

/**
 * Dagger module for use case injection
 */
@Module
class UseCaseModule {

    @Provides
    fun provideUseCaseThreadPoolScheduler() : UseCaseScheduler {
        return UseCaseThreadPoolScheduler()
    }

    @Provides
    fun provideUseCaseHandler(useCaseScheduler: UseCaseScheduler) : UseCaseHandler {
        return UseCaseHandler(useCaseScheduler)
    }

    @Provides
    fun provideGetFlashcard(flashcardRepository: FlashcardRepository) : GetFlashcard {
        return GetFlashcard(flashcardRepository)
    }

    @Provides
    fun provideGetFlashcards(flashcardRepository: FlashcardRepository) : GetFlashcards {
        return GetFlashcards(flashcardRepository)
    }

    @Provides
    fun provideSaveFlashcard(flashcardRepository: FlashcardRepository) : SaveFlashcard {
        return SaveFlashcard(flashcardRepository)
    }

    @Provides
    fun provideGetRandomFlashcard(flashcardRepository: FlashcardRepository) : GetRandomFlashcard {
        return GetRandomFlashcard(flashcardRepository)
    }
}
