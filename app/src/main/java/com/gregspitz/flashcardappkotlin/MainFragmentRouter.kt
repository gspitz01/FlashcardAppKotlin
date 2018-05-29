package com.gregspitz.flashcardappkotlin

/**
 * An interface for routing between the fragments in the main activity
 */
interface MainFragmentRouter {

    fun showFlashcardList(flashcardId: String)

    fun showAddEditFlashcard(flashcardId: String)

    fun showRandomFlashcard()
}