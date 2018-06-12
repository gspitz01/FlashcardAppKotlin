package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService

class GetDownloadCategories(private val flashcardDownloadService: FlashcardDownloadService)
    : UseCase<GetDownloadCategories.RequestValues, GetDownloadCategories.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        flashcardDownloadService.getDownloadCategories(
                object: FlashcardDownloadService.GetDownloadCategoriesCallback {
                    override fun onCategoriesLoaded(
                            categoryToNumberOfFlashcardsMap: Map<Category, Int>) {

                        getUseCaseCallback()
                                .onSuccess(ResponseValue(categoryToNumberOfFlashcardsMap))
                    }

                    override fun onDataNotAvailable() {
                        getUseCaseCallback().onError()
                    }
                })
    }

    class RequestValues : UseCase.RequestValues

    class ResponseValue(val categoryToNumberOfFlashcardsMap: Map<Category, Int>) : UseCase.ResponseValue
}
