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
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.model.FlashcardPriorityProbabilityDistribution
import java.util.*
import javax.inject.Inject

/**
 * Use case for retrieving a random flashcard while avoiding returning the previously seen card
 */
class GetRandomFlashcard @Inject constructor(
        private val flashcardRepository: FlashcardRepository,
        private val probabilityDistribution: FlashcardPriorityProbabilityDistribution)
    : UseCase<GetRandomFlashcard.RequestValues, GetRandomFlashcard.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        flashcardRepository.getFlashcards(RandomFlashcardRepoGet(
                requestValues.flashcardId, getUseCaseCallback()))
    }

    class RequestValues(val flashcardId: String?) : UseCase.RequestValues

    class ResponseValue(val flashcard: Flashcard) : UseCase.ResponseValue

    /**
     * Get a random Flashcard from the repo
     */
    inner class RandomFlashcardRepoGet(
            private val flashcardId: String?,
            private val useCaseCallback: UseCaseCallback<ResponseValue>
    ) : FlashcardDataSource.GetFlashcardsCallback {

        private val random = Random()

        override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
            when {
                flashcards.size > 1 -> {

                    var flashcard: Flashcard?
                    // Keep track of number attempts just in case (see below)
                    var attempts = 0
                    var broke = false
                    do {
                        // Pick a priority based on distribution
                        val priority = choosePriority(flashcards)
                        // Then pick randomly from within that priority group
                        val flashcardsOfPriority =
                                flashcards.filter { it.priority == priority }

                        // If more than one Flashcard of that priority, choose randomly between them
                        // while avoiding the previously retrieved Flashcard
                        val index = random.nextInt(flashcardsOfPriority.size)
                        flashcard = flashcardsOfPriority[index]
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

        private fun choosePriority(flashcards: List<Flashcard>): FlashcardPriority {
            // Extant priorities are ones for which there are Flashcards in the list
            val extantPriorityProbabilities =
                    mutableMapOf<FlashcardPriority, Double>()
            for (priority in FlashcardPriority.values()) {
                if (flashcards.any { it.priority == priority }) {
                    extantPriorityProbabilities[priority] =
                            probabilityDistribution.getDistributionMap()[priority] ?: 0.0
                } else {
                    extantPriorityProbabilities[priority] = 0.0
                }
            }
            // Choose a random number between 0 and the sum of the extant priorities
            val randomDouble = random.nextDouble() * extantPriorityProbabilities.values.sum()

            val sumToUrgent = extantPriorityProbabilities[FlashcardPriority.NEW]!! +
                    extantPriorityProbabilities[FlashcardPriority.URGENT]!!
            val sumToHigh = sumToUrgent +
                    extantPriorityProbabilities[FlashcardPriority.HIGH]!!
            val sumToMedium = sumToHigh +
                    extantPriorityProbabilities[FlashcardPriority.MEDIUM]!!
            // Select priority based on where random number falls in distribution
            return when {
                randomDouble < extantPriorityProbabilities[FlashcardPriority.NEW]!! -> {
                    FlashcardPriority.NEW
                }
                randomDouble < sumToUrgent -> FlashcardPriority.URGENT
                randomDouble < sumToHigh -> FlashcardPriority.HIGH
                randomDouble < sumToMedium -> FlashcardPriority.MEDIUM
                else -> FlashcardPriority.LOW
            }
        }

    }
}
