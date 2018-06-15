package com.gregspitz.flashcardappkotlin.flashcarddownload

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.DownloadFlashcards
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.GetDownloadCategories

class FlashcardDownloadPresenter(
        private val useCaseHandler: UseCaseHandler,
        private val view: FlashcardDownloadContract.View,
        private val getDownloadCategories: GetDownloadCategories,
        private val downloadFlashcards: DownloadFlashcards)
    : FlashcardDownloadContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun loadDownloadCategories() {
        view.setLoadingIndicator(true)

        useCaseHandler.execute(getDownloadCategories, GetDownloadCategories.RequestValues(),
                object: UseCase.UseCaseCallback<GetDownloadCategories.ResponseValue> {
                    override fun onSuccess(response: GetDownloadCategories.ResponseValue) {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            view.showDownloadCategories(response.downloadCategories)
                        }
                    }

                    override fun onError() {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            view.showFailedToGetDownloadCategories()
                        }
                    }
                })
    }

    override fun downloadFlashcards(categories: List<DownloadCategoryFlexItem>) {
        view.setLoadingIndicator(true)

        useCaseHandler.execute(downloadFlashcards, DownloadFlashcards.RequestValues(categories),
                object: UseCase.UseCaseCallback<DownloadFlashcards.ResponseValue> {
                    override fun onSuccess(response: DownloadFlashcards.ResponseValue) {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            view.showFlashcardDownloadSuccessful()
                        }
                    }

                    override fun onError() {
                        if (view.isActive()) {
                            view.setLoadingIndicator(false)
                            view.showFlashcardDownloadFailure()
                        }
                    }
                })
    }

    override fun start() {
        loadDownloadCategories()
    }
}
