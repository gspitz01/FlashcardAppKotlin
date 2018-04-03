package com.gregspitz.flashcardappkotlin.addeditflashcard

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Contract between AddEditFlashcard view and its presenter
 */
interface AddEditFlashcardContract {

    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun getIdFromIntent() : String

        fun showFlashcard(flashcard: Flashcard)

        fun showNewFlashcard()

        fun showFlashcardList()

        fun showFailedToLoadFlashcard()

        fun showSaveSuccessful()

        fun showSaveFailed()

        fun isActive() : Boolean
    }

    interface Presenter : BasePresenter {

        fun loadFlashcard(flashcardId: String)

        fun saveFlashcard(flashcard: Flashcard)

        fun showList()
    }
}
