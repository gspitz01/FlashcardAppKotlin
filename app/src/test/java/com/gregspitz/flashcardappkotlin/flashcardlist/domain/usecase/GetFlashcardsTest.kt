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

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_2
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link GetFlashcards}
 */
class GetFlashcardsTest {

    // Another Flashcard with the same category as FLASHCARD_1 and a front which sorts to before FLASHCARD_1's
    private val flashcard7 = Flashcard("7", FLASHCARD_1.category, "A front", "Back",
            FlashcardPriority.NEW)

    private val noCategoryRequest = GetFlashcards.RequestValues()
    private val categoryRequest =
            GetFlashcards.RequestValues(FLASHCARD_1.category)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

    private val callback: UseCase.UseCaseCallback<GetFlashcards.ResponseValue> = mock()

    private val responseCaptor = argumentCaptor<GetFlashcards.ResponseValue>()

    private lateinit var getFlashcards: GetFlashcards

    @Before
    fun setup() {
        getFlashcards = GetFlashcards(flashcardRepository)
    }

    @Test
    fun `gets flashcards without category from repository and on success calls success on callback with sorted flashcards`() {
        executeUseCaseWithoutCategory()
        val unsortedFlashcardList = listOf(FLASHCARD_1, FLASHCARD_2,
                flashcard7)
        val sortedFlashcardListWithCategories = listOf(CATEGORY_1, flashcard7,
                FLASHCARD_1, CATEGORY_2, FLASHCARD_2)
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(unsortedFlashcardList)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(sortedFlashcardListWithCategories, responseCaptor.firstValue.flashcards)
    }

    @Test
    fun `gets flashcards without category from repository and on failure calls error on callback`() {
        executeUseCaseWithoutCategory()
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }

    @Test
    fun `gets flashcards with category from repository and on success calls success on callback with sorted flashcards`() {
        executeUseCaseWithCategory()
        val unsortedFlashcardList = listOf(FLASHCARD_1, flashcard7)
        val sortedFlashcardListWithCategories =
                listOf(CATEGORY_1, flashcard7, FLASHCARD_1)
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(unsortedFlashcardList)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(sortedFlashcardListWithCategories, responseCaptor.firstValue.flashcards)
    }

    @Test
    fun `gets flashcards with category from repository and on failure calls error on callback`() {
        executeUseCaseWithCategory()
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }

    private fun executeUseCaseWithoutCategory() {
        useCaseHandler.execute(getFlashcards, noCategoryRequest, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
    }

    private fun executeUseCaseWithCategory() {
        useCaseHandler.execute(getFlashcards, categoryRequest, callback)
        verify(flashcardRepository).getFlashcardsByCategoryName(eq(categoryRequest.categoryName!!),
                repositoryCallbackCaptor.capture())
    }
}
