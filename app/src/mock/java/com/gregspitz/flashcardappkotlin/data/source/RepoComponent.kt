package com.gregspitz.flashcardappkotlin.data.source

import android.support.annotation.VisibleForTesting
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.di.AppModule
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component for injecting FlashcardRepository
 */
@Singleton
@Component(modules = [(AppModule::class), (RepoModule::class)])
interface RepoComponent {

    fun inject(saveFlashcard: SaveFlashcard)

    fun inject(getRandomFlashcard: GetRandomFlashcard)

    fun inject(getFlashcards: GetFlashcards)

    fun inject(getFlashcard: GetFlashcard)

    fun exposeRepository() : FlashcardRepository

    @VisibleForTesting
    fun getFlashcardLocalDataSource() : FakeFlashcardLocalDataSource
}
