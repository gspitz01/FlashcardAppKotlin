package com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository

class DeleteFlashcards(private val repository: FlashcardRepository)
    : UseCase<DeleteFlashcards.RequestValues, DeleteFlashcards.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        if (requestValues.categoryName == null) {
            repository.deleteAllFlashcards(object: FlashcardDataSource.DeleteAllFlashcardsCallback {
                override fun onDeleteSuccessful() {
                    getUseCaseCallback().onSuccess(ResponseValue())
                }

                override fun onDeleteFailed() {
                    getUseCaseCallback().onError()
                }
            })
        } else {
            repository.deleteFlashcardsByCategoryName(requestValues.categoryName,
                    object: FlashcardDataSource.DeleteFlashcardsByCategoryNameCallback {
                        override fun onDeleteSuccessful() {
                            getUseCaseCallback().onSuccess(ResponseValue())
                        }

                        override fun onDeleteFailed() {
                            getUseCaseCallback().onError()
                        }
                    })
        }
    }

    class RequestValues(val categoryName: String? = null) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
