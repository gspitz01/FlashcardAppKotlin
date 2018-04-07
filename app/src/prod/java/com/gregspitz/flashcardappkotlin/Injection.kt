package com.gregspitz.flashcardappkotlin

import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import javax.inject.Inject

/**
 * Production injection
 */
object Injection {

    // TODO: get rid of this entirely and only use Dagger

    @set:Inject
    lateinit var flashcardRepository: FlashcardRepository

    init {
        FlashcardApplication.repoComponent.inject(this)
    }

    fun provideUseCaseHandler() : UseCaseHandler {
        return UseCaseHandler.getInstance(UseCaseThreadPoolScheduler())
    }

    fun provideGetFlashcards() : GetFlashcards {
        return GetFlashcards(flashcardRepository)
    }

    fun provideGetFlashcard() : GetFlashcard {
        return GetFlashcard(flashcardRepository)
    }

    fun provideSaveFlashcard() : SaveFlashcard {
        return SaveFlashcard(flashcardRepository)
    }

    fun provideGetRandomFlashcard() : GetRandomFlashcard {
        return GetRandomFlashcard(flashcardRepository)
    }
}
