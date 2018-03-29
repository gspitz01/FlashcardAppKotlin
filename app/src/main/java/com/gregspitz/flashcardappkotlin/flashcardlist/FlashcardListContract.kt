package com.gregspitz.flashcardappkotlin.flashcardlist

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView
import com.gregspitz.flashcardappkotlin.data.model.Flashcard

/**
 * A contract between the flashcard list view and its presenter
 */

interface FlashcardListContract {
    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun showFlashcards(flashcards: List<Flashcard>)

        fun showFailedToLoadFlashcards()

        fun showNoFlashcardsToLoad()

        fun showAddFlashcard()

        fun showFlashcardDetailsUi(flashcardId: String)

        fun isActive(): Boolean
    }

    interface Presenter : BasePresenter {

        fun selectFlashcard(flashcard: Flashcard)

        fun addFlashcard()

        fun loadFlashcards()

        fun onFlashcardClick(flashcardId: String)
    }
}
