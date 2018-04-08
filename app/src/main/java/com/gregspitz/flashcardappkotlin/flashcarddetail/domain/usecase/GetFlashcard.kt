package com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import javax.inject.Inject

/**
 * UseCase for getting a single Flashcard
 */
class GetFlashcard @Inject constructor(private val flashcardRepository: FlashcardRepository)
    : UseCase<GetFlashcard.RequestValues, GetFlashcard.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

        flashcardRepository.getFlashcard(requestValues.flashcardId,
                object: FlashcardDataSource.GetFlashcardCallback {
                    override fun onFlashcardLoaded(flashcard: Flashcard) {
                        getUseCaseCallback().onSuccess(ResponseValue(flashcard))
                    }

                    override fun onDataNotAvailable() {
                        getUseCaseCallback().onError()
                    }

                })
    }

    class RequestValues(val flashcardId: String) : UseCase.RequestValues

    class ResponseValue(val flashcard: Flashcard) : UseCase.ResponseValue
}
