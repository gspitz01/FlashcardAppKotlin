/*
 * Copyright (C) 2018 Greg Spitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import org.uncommons.maths.random.ExponentialGenerator
import java.util.*
import javax.inject.Inject

private const val EXPONENTIAL_DISTRIBUTION_RATE = 0.5

/**
 * Use case for retrieving a random flashcard while avoiding returning the previously seen card
 */
class GetRandomFlashcard @Inject constructor(
        private val flashcardRepository: FlashcardRepository)
    : UseCase<GetRandomFlashcard.RequestValues, GetRandomFlashcard.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        if (requestValues.categoryName == null) {
            flashcardRepository.getFlashcards(RandomFlashcardRepoGet(
                    requestValues.flashcardId, getUseCaseCallback()))
        } else {
            flashcardRepository.getFlashcardsByCategoryName(requestValues.categoryName,
                    RandomFlashcardRepoGet(requestValues.flashcardId, getUseCaseCallback()))
        }
    }

    class RequestValues(val flashcardId: String? = null, val categoryName: String? = null)
        : UseCase.RequestValues

    class ResponseValue(val flashcard: Flashcard) : UseCase.ResponseValue

    /**
     * Get a random Flashcard from the repo
     */
    inner class RandomFlashcardRepoGet(
            private val flashcardId: String?,
            private val useCaseCallback: UseCaseCallback<ResponseValue>
    ) : FlashcardDataSource.GetFlashcardsCallback {

        // Use an exponential distribution to prioritize Flashcards with lower priority number
        private val exponentialGenerator =
                ExponentialGenerator(EXPONENTIAL_DISTRIBUTION_RATE, Random())

        override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {

            when {
                flashcards.size > 1 -> {
                    // Sort the Flashcards by priority
                    val sortedFlashcards = sortFlashcards(flashcards)

                    var flashcard: Flashcard?
                    // Keep track of number attempts just in case (see below)
                    var attempts = 0
                    var broke = false
                    do {
                        // Select a random Flashcard based on distribution
                        // while avoiding the previously retrieved Flashcard
                        val index = getRandomDistributedSelection(sortedFlashcards.size)
                        flashcard = sortedFlashcards[index]
                        attempts++
                        // In case somehow all flashcards have the same id, which shouldn't happen,
                        // but don't want to end up in an infinite loop
                        if (attempts > 20) {
                            broke = true
                            break
                        }
                    } while (flashcard == null || flashcard.id == flashcardId)
                    if (broke) {
                        // Too many attempts, just choose the first flashcard
                        useCaseCallback.onSuccess(ResponseValue(flashcards[0]))
                    } else {
                        // Can be sure that flashcard isn't actually null here
                        useCaseCallback.onSuccess(ResponseValue(flashcard!!))
                    }
                }
                flashcards.size == 1 -> {
                    // if only one Flashcard, just return it
                    useCaseCallback.onSuccess(ResponseValue(flashcards[0]))
                }
                else -> useCaseCallback.onError()
            }
        }

        override fun onDataNotAvailable() {
            useCaseCallback.onError()
        }

        fun sortFlashcards(flashcards: List<Flashcard>): List<Flashcard> =
                flashcards.sortedWith(compareBy { it.priority })

        fun getRandomDistributedSelection(size: Int): Int {
            var randomDouble = 1.0
            // Basically just truncating the distribution so it doesn't go out of bounds
            while (randomDouble >= 1.0) {
                randomDouble = exponentialGenerator.nextValue()
            }
            val inverseSize = 1.0 / size
            return Math.floor(randomDouble / inverseSize).toInt()
        }
    }
}
