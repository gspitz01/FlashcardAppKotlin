package com.gregspitz.flashcardappkotlin.categorylist

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.categorylist.domain.usecase.GetCategories

class CategoryListPresenter(private val useCaseHandler: UseCaseHandler,
                            private val view: CategoryListContract.View,
                            private val viewModel: CategoryListContract.ViewModel,
                            private val getCategories: GetCategories)
    : CategoryListContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun loadCategories() {
        if (view.isActive()) {
            view.setLoadingIndicator(true)
        }
        useCaseHandler.execute(getCategories, GetCategories.RequestValues(),
                object: UseCase.UseCaseCallback<GetCategories.ResponseValue> {
                    override fun onSuccess(response: GetCategories.ResponseValue) {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            if (response.categories.isEmpty()) {
                                view.showNoCategoriesToLoad()
                            } else {
                                viewModel.setCategories(response.categories)
                            }
                        }
                    }

                    override fun onError() {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            view.showFailedToLoadCategories()
                        }
                    }
                })
    }

    override fun onCategoryClick(recyclerPosition: Int) {
        view.showFlashcardList(recyclerPosition)
    }

    override fun start() {
        loadCategories()
    }

}
