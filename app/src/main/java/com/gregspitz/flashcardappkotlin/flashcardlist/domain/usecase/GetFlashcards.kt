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

package com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardListItem
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import javax.inject.Inject

/**
 * A use case for retrieving all available flashcards
 * or all flashcards in a particular category
 */
class GetFlashcards @Inject constructor(private val flashcardRepository: FlashcardRepository)
    : UseCase<GetFlashcards.RequestValues, GetFlashcards.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        if (requestValues.categoryName == null) {
            flashcardRepository.getFlashcards(object : FlashcardDataSource.GetFlashcardsCallback {
                override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
                    val sortedFlashcards = sortFlashcards(flashcards)
                    getUseCaseCallback()
                            .onSuccess(ResponseValue(createListWithCategories(sortedFlashcards)))
                }

                override fun onDataNotAvailable() {
                    getUseCaseCallback().onError()
                }

            })
        } else {
            flashcardRepository.getFlashcardsByCategoryName(requestValues.categoryName,
                    object : FlashcardDataSource.GetFlashcardsCallback {
                        override fun onFlashcardsLoaded(flashcards: List<Flashcard>) {
                            val sortedFlashcards = sortFlashcards(flashcards)
                            getUseCaseCallback()
                                    .onSuccess(ResponseValue(
                                            createListWithCategories(sortedFlashcards)))
                        }

                        override fun onDataNotAvailable() {
                            getUseCaseCallback().onError()
                        }
                    })
        }
    }

    fun createListWithCategories(flashcards: List<Flashcard>): List<FlashcardListItem> {
        val listWithCategories = mutableListOf<FlashcardListItem>()
        for (flashcard in flashcards) {
            if (!listWithCategories.contains(Category(flashcard.category))) {
                listWithCategories.add(Category(flashcard.category))
            }
            listWithCategories.add(flashcard)
        }
        return listWithCategories
    }

    /**
     * Sort Flashcards by category and front
     * @param flashcards List of Flashcards to be sorted
     */
    fun sortFlashcards(flashcards: List<Flashcard>): List<Flashcard> =
            flashcards.sortedWith(compareBy({ it.category }, { it.front }))

    class RequestValues(val categoryName: String? = null) : UseCase.RequestValues

    class ResponseValue(val flashcards: List<FlashcardListItem>) : UseCase.ResponseValue
}
