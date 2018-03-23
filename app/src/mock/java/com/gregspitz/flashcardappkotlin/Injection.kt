package com.gregspitz.flashcardappkotlin

import android.content.Context
import com.gregspitz.flashcardappkotlin.data.source.FakeFlashcardLocalDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards

/**
 * Dependency Injection object
 */
object Injection {

    fun provideFlashcardRepository(context: Context) : FlashcardRepository {
        return FlashcardRepository.getInstance(FakeFlashcardLocalDataSource.getInstance(context))
    }

    fun provideUseCaseHandler() : UseCaseHandler {
        return UseCaseHandler.getInstance(UseCaseThreadPoolScheduler())
    }

    fun provideGetFlashcards(context: Context) : GetFlashcards {
        return GetFlashcards(provideFlashcardRepository(context))
    }
}
