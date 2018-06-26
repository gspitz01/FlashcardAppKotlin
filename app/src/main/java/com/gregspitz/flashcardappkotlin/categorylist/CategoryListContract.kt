package com.gregspitz.flashcardappkotlin.categorylist

import com.gregspitz.flashcardappkotlin.BasePresenter
import com.gregspitz.flashcardappkotlin.BaseView
import com.gregspitz.flashcardappkotlin.data.model.Category

interface CategoryListContract {

    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun showFailedToLoadCategories()

        fun showNoCategoriesToLoad()

        fun showFlashcardList(recyclerPosition: Int)

        fun isActive(): Boolean
    }

    interface Presenter : BasePresenter {

        fun loadCategories()

        fun onCategoryClick(recyclerPosition: Int)
    }

    interface ViewModel {

        fun setCategories(categories: List<Category>)

        fun getCategories(): List<Category>?
    }
}
