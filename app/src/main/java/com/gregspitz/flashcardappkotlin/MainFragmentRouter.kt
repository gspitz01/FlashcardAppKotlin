package com.gregspitz.flashcardappkotlin

/**
 * An interface for routing between the fragments in the main activity
 */
interface MainFragmentRouter {

    fun showFlashcardList(flashcardId: String)

    fun showCategoryFlashcardList(categoryName: String, flashcardId: String? = null)

    fun showCategoryList()

    fun showAddEditFlashcard(flashcardId: String)

    fun showRandomFlashcard(categoryName: String?)

    fun showFlashcardDownload()

    fun showSnackbar(stringId: Int)
}
