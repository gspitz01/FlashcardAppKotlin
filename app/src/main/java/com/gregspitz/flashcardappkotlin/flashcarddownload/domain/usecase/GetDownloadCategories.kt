package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory

class GetDownloadCategories(private val flashcardDownloadService: FlashcardDownloadService)
    : UseCase<GetDownloadCategories.RequestValues, GetDownloadCategories.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        flashcardDownloadService.getDownloadCategories(
                object: FlashcardDownloadService.GetDownloadCategoriesCallback {
                    override fun onCategoriesLoaded(
                            downloadCategories: List<DownloadCategory>) {

                        getUseCaseCallback()
                                .onSuccess(ResponseValue(downloadCategories))
                    }

                    override fun onDataNotAvailable() {
                        getUseCaseCallback().onError()
                    }
                })
    }

    class RequestValues : UseCase.RequestValues

    class ResponseValue(val downloadCategories: List<DownloadCategory>) : UseCase.ResponseValue
}
