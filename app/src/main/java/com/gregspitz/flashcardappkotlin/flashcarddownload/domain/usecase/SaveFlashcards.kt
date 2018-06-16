package com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository

class SaveFlashcards(private val flashcardRepository: FlashcardRepository)
    : UseCase<SaveFlashcards.RequestValues, SaveFlashcards.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        flashcardRepository.saveFlashcards(requestValues.flashcards,
                object: FlashcardDataSource.SaveFlashcardsCallback {
                    override fun onSaveSuccessful() {
                        getUseCaseCallback().onSuccess(ResponseValue())
                    }

                    override fun onSaveFailed() {
                        getUseCaseCallback().onError()
                    }
                })
    }

    class RequestValues(val flashcards: List<Flashcard>) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
