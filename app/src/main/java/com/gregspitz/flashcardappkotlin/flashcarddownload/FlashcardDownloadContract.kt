package com.gregspitz.flashcardappkotlin.flashcarddownload

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory

interface FlashcardDownloadContract {

    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun showDownloadCategories(downloadCategories: List<DownloadCategory>)

        fun showFailedToGetDownloadCategories()

        fun showFlashcardDownloadSuccessful()

        fun showFlashcardDownloadFailure()

        fun isActive() : Boolean
    }

    interface Presenter : BasePresenter {

        fun loadDownloadCategories()

        fun downloadFlashcards(categories: List<Category>)
    }
}
