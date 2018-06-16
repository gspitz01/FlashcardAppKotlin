package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadFlashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcarddownload.DownloadCategoryFlexItem

class DownloadFlashcards(private val downloadService: FlashcardDownloadService,
                         private val repository: FlashcardRepository)
    : UseCase<DownloadFlashcards.RequestValues, DownloadFlashcards.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        val categories = requestValues.categories.map { it.downloadCategory }
        downloadService.downloadFlashcardsByCategory(categories,
                object: FlashcardDownloadService.DownloadFlashcardsCallback {
                    override fun onFlashcardsDownloaded(downloadFlashcards: List<DownloadFlashcard>) {
                        val flashcards = downloadFlashcards.map {
                            Flashcard(it.id, it.category.name, it.front, it.back)
                        }
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

    class RequestValues(val categories: List<DownloadCategoryFlexItem>) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
