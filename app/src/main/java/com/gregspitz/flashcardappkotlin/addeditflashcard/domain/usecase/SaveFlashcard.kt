package com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository

/**
 * Use case for saving a flashcard
 */
class SaveFlashcard(private val flashcardRepository: FlashcardRepository)
    : UseCase<SaveFlashcard.RequestValues, SaveFlashcard.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        print("Saving flashcard")
        flashcardRepository.saveFlashcard(requestValues.flashcard,
                object: FlashcardDataSource.SaveFlashcardCallback {
                    override fun onSaveSuccessful() {
                        getUseCaseCallback().onSuccess(ResponseValue())
                    }

                    override fun onSaveFailed() {
                        getUseCaseCallback().onError()
                    }

                })
    }

    class RequestValues(val flashcard: Flashcard) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
