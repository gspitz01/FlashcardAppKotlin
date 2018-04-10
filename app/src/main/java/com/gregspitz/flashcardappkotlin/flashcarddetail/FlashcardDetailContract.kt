package com.gregspitz.flashcardappkotlin.flashcarddetail

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * Contract between FlashcardDetail view and its presenter
 */
interface FlashcardDetailContract {
    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun showEditFlashcard(flashcardId: String)

        fun showFailedToLoadFlashcard()

        fun getIdFromIntent() : String

        fun isActive() : Boolean
    }

    interface Presenter : BasePresenter {

        fun loadFlashcard(flashcardId: String)

        fun editFlashcard()
    }

    interface ViewModel {

        fun setFlashcard(flashcard: Flashcard)

        fun getFlashcard() : Flashcard?
    }
}
