package com.gregspitz.flashcardappkotlin.randomflashcard

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide

/**
 * Contract between RandomFlashcard view and its presenter
 */
interface RandomFlashcardContract {

    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun showFailedToLoadFlashcard()

        fun isActive() : Boolean
    }

    interface Presenter : BasePresenter {

        fun turnFlashcard()

        fun loadNewFlashcard()
    }

    interface ViewModel {

        fun setFlashcard(flashcard: Flashcard)

        fun setFlashcardSide(flashcardSide: FlashcardSide)

        fun getFlashcard() : Flashcard?

        fun getFlashcardSide() : FlashcardSide?
    }
}
