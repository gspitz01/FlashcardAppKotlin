package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository

class DownloadFlashcards(private val downloadService: FlashcardDownloadService,
                         private val repository: FlashcardRepository)
    : UseCase<DownloadFlashcards.RequestValues, DownloadFlashcards.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        downloadService.downloadFlashcardsByCategory(requestValues.categories,
                object: FlashcardDownloadService.DownloadFlashcardsCallback {
                    override fun onFlashcardsDownloaded(flashcards: List<Flashcard>) {
                        repository.saveFlashcards(flashcards,
                                object: FlashcardDataSource.SaveFlashcardsCallback {
                                    override fun onSaveSuccessful() {
                                        getUseCaseCallback().onSuccess(ResponseValue())
                                    }

                                    override fun onSaveFailed() {
                                        getUseCaseCallback().onError()
                                    }
                                })
                    }

                    override fun onDataNotAvailable() {
                        getUseCaseCallback().onError()
                    }
                })
    }

    class RequestValues(val categories: List<DownloadCategory>) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
