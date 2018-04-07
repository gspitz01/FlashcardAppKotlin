package com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository

/**
 * A use case for retrieving all available flashcards
 */
class GetFlashcards(private val flashcardRepository: FlashcardRepository)
    : UseCase<GetFlashcards.RequestValues, GetFlashcards.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        flashcardRepository.getFlashcards(object: FlashcardDataSource.GetFlashcardsCallback {
            override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
                getUseCaseCallback().onSuccess(ResponseValue(flashcards))
            }

            override fun onDataNotAvailable() {
                getUseCaseCallback().onError()
            }

        })
    }

    class RequestValues : UseCase.RequestValues

    class ResponseValue(val flashcards: List<Flashcard>) : UseCase.ResponseValue
}
