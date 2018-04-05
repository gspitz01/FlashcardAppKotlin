package com.gregspitz.flashcardappkotlin

import android.content.Context
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.data.source.local.FlashcardDao
import com.gregspitz.flashcardappkotlin.data.source.local.FlashcardDatabase
import com.gregspitz.flashcardappkotlin.data.source.local.FlashcardLocalDataSource
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard

/**
 * Production injection
 */
object Injection {
    fun provideFlashcardRepository(context: Context) : FlashcardRepository {
        return FlashcardRepository.getInstance(
                FlashcardLocalDataSource.getInstance(
                        provideFlashcardDao(provideFlashcardDatabase(context))))
    }

    fun provideFlashcardDao(flashcardDatabase: FlashcardDatabase) : FlashcardDao {
        return flashcardDatabase.flashcardDao()
    }

    fun provideFlashcardDatabase(context: Context) : FlashcardDatabase {
        return FlashcardDatabase.getInstance(context)!!
    }

    fun provideUseCaseHandler() : UseCaseHandler {
        return UseCaseHandler.getInstance(UseCaseThreadPoolScheduler())
    }

    fun provideGetFlashcards(context: Context) : GetFlashcards {
        return GetFlashcards(provideFlashcardRepository(context))
    }

    fun provideGetFlashcard(context: Context) : GetFlashcard {
        return GetFlashcard(provideFlashcardRepository(context))
    }

    fun provideSaveFlashcard(context: Context) : SaveFlashcard {
        return SaveFlashcard(provideFlashcardRepository(context))
    }

    fun provideGetRandomFlashcard(context: Context) : GetRandomFlashcard {
        return GetRandomFlashcard(provideFlashcardRepository(context))
    }
}
