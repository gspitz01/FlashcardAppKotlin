package com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import java.util.*

/**
 * Use case for retrieving a random flashcard while avoiding returning the previously seen card
 */
class GetRandomFlashcard(private val flashcardRepository: FlashcardRepository)
    : UseCase<GetRandomFlashcard.RequestValues, GetRandomFlashcard.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        flashcardRepository.getFlashcards(RandomFlashcardRepoGet(
                requestValues.flashcardId, getUseCaseCallback()))
    }

    class RequestValues(val flashcardId: String?) : UseCase.RequestValues

    class ResponseValue(val flashcard: Flashcard) : UseCase.ResponseValue

    inner class RandomFlashcardRepoGet(
            private val flashcardId: String?,
            private val useCaseCallback: UseCaseCallback<ResponseValue>
    ) : FlashcardDataSource.GetFlashcardsCallback {

        private val random = Random()

        override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
            when {
                flashcards.size > 1 -> {
                    var flashcard: Flashcard?
                    var attempts = 0
                    var broke = false
                    do {
                        val index = random.nextInt(flashcards.size)
                        flashcard = flashcards[index]
                        attempts++
                        // In case somehow all flashcards have the same id, which shouldn't happen,
                        // but don't want to end up in an infinite loop
                        if (attempts > 20) {
                            broke = true
                            break
                        }
                    } while (flashcard == null || flashcard.id == flashcardId)
                    if (broke) {
                        // For some reason all the flashcards had the same id
                        // So just return the first one
                        useCaseCallback.onSuccess(ResponseValue(flashcards[0]))
                    } else {
                        // Can be sure that flashcard isn't actually null here
                        useCaseCallback.onSuccess(ResponseValue(flashcard!!))
                    }
                }
                flashcards.size == 1 -> {
                    useCaseCallback.onSuccess(ResponseValue(flashcards[0]))
                }
                else -> useCaseCallback.onError()
            }
        }

        override fun onDataNotAvailable() {
            useCaseCallback.onError()
        }

    }
}
