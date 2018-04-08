package com.gregspitz.flashcardappkotlin.di

import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardActivity
import com.gregspitz.flashcardappkotlin.data.source.RepoComponent
import com.gregspitz.flashcardappkotlin.data.source.RepoModule
import com.gregspitz.flashcardappkotlin.flashcarddetail.FlashcardDetailActivity
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListActivity
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger component for use case injection
 */
@UseCaseScope
@Component(modules = [(UseCaseModule::class)], dependencies = [(RepoComponent::class)])
interface UseCaseComponent {

    fun inject(addEditFlashcardActivity: AddEditFlashcardActivity)

    fun inject(flashcardDetailActivity: FlashcardDetailActivity)

    fun inject(flashcardListActivity: FlashcardListActivity)

    fun inject(randomFlashcardActivity: RandomFlashcardActivity)
}
