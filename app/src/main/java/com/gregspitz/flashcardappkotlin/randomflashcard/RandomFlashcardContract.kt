package com.gregspitz.flashcardappkotlin.randomflashcard

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView

/**
 * Contract between RandomFlashcard view and its presenter
 */
interface RandomFlashcardContract {

    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun showFlashcardSide(side: String)

        fun showFailedToLoadFlashcard()

        fun isActive() : Boolean
    }

    interface Presenter : BasePresenter {

        fun turnFlashcard()

        fun loadNewFlashcard()
    }
}
