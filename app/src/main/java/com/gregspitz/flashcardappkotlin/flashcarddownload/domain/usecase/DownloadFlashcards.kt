package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
import com.gregspitz.flashcardappkotlin.data.service.FlashcardDownloadService
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadFlashcard
import com.gregspitz.flashcardappkotlin.flashcarddownload.DownloadCategoryFlexItem

class DownloadFlashcards(private val downloadService: FlashcardDownloadService)
    : UseCase<DownloadFlashcards.RequestValues, DownloadFlashcards.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        val category = requestValues.category.downloadCategory
        downloadService.downloadFlashcardsByCategory(category,
                object: FlashcardDownloadService.DownloadFlashcardsCallback {
                    override fun onFlashcardsDownloaded(downloadFlashcards: List<DownloadFlashcard>) {
                        val flashcards = downloadFlashcards.map {
                            Flashcard(it.id, it.category_name, it.front, it.back,
                                    FlashcardPriority.NEW)
                        }
                        getUseCaseCallback().onSuccess(ResponseValue(flashcards))
                    }

                    override fun onDataNotAvailable() {
                        getUseCaseCallback().onError()
                    }
                })
    }

    class RequestValues(val category: DownloadCategoryFlexItem) : UseCase.RequestValues

    class ResponseValue(val flashcards: List<Flashcard>) : UseCase.ResponseValue
}
