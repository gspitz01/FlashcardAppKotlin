package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.flashcarddownload.DownloadCategoryFlexItem

class GetDownloadCategories(private val flashcardDownloadService: FlashcardDownloadService)
    : UseCase<GetDownloadCategories.RequestValues, GetDownloadCategories.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        flashcardDownloadService.getDownloadCategories(
                object: FlashcardDownloadService.GetDownloadCategoriesCallback {
                    override fun onCategoriesLoaded(
                            downloadCategories: List<DownloadCategory>) {
                        val flexItems = downloadCategories.map {
                            DownloadCategoryFlexItem(it)
                        }
                        getUseCaseCallback()
                                .onSuccess(ResponseValue(flexItems))
                    }

                    override fun onDataNotAvailable() {
                        getUseCaseCallback().onError()
                    }
                })
    }

    class RequestValues : UseCase.RequestValues

    class ResponseValue(val downloadCategories: List<DownloadCategoryFlexItem>) : UseCase.ResponseValue
}
