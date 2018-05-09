package com.gregspitz.flashcardappkotlin.di

import com.gregspitz.flashcardappkotlin.addeditflashcard.AddEditFlashcardActivity
import com.gregspitz.flashcardappkotlin.data.source.RepoComponent
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardDetailFragment
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListActivity
import com.gregspitz.flashcardappkotlin.randomflashcard.RandomFlashcardActivity
import dagger.Component

/**
 * Dagger component for use case injection
 */
@UseCaseScope
@Component(modules = [(UseCaseModule::class)], dependencies = [(RepoComponent::class)])
interface UseCaseComponent {

    fun inject(addEditFlashcardActivity: AddEditFlashcardActivity)

    fun inject(flashcardListActivity: FlashcardListActivity)

    fun inject(randomFlashcardActivity: RandomFlashcardActivity)
}
