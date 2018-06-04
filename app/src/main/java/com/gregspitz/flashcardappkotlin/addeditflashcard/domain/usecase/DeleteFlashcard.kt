package com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository

class DeleteFlashcard(private val flashcardRepository: FlashcardRepository)
    : UseCase<DeleteFlashcard.RequestValues, DeleteFlashcard.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        flashcardRepository.deleteFlashcard(requestValues.flashcardId,
                object: FlashcardDataSource.DeleteFlashcardCallback {
                    override fun onDeleteSuccessful() {
                        getUseCaseCallback().onSuccess(ResponseValue())
                    }

                    override fun onDeleteFailed() {
                        getUseCaseCallback().onError()
                    }
                })
    }


    class RequestValues(val flashcardId: String) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
