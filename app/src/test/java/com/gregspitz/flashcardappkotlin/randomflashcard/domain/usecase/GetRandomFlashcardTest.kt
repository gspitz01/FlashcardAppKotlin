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

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST_OF_CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST_SAME_IDS
import com.gregspitz.flashcardappkotlin.TestData.SINGLE_FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link GetRandomFlashcard}
 */
class GetRandomFlashcardTest {

    // Request values represents the id of the previous Flashcard
    // Null means there was no previous Flashcard
    private val requestValuesNull =
            GetRandomFlashcard.RequestValues()
    private val requestValuesFlashcard1 =
            GetRandomFlashcard.RequestValues(FLASHCARD_1.id)
    private val requestValuesCategory =
            GetRandomFlashcard.RequestValues(categoryName = CATEGORY_1.name)
    private val requestValuesCategoryAndPrevious =
            GetRandomFlashcard.RequestValues(FLASHCARD_1.id, CATEGORY_1.name)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

    private val callback: UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue> = mock()

    private val responseCaptor =
            argumentCaptor<GetRandomFlashcard.ResponseValue>()

    private lateinit var getRandomFlashcard: GetRandomFlashcard
    private lateinit var randomGet: GetRandomFlashcard.RandomFlashcardRepoGet

    @Before
    fun setup() {
        getRandomFlashcard = GetRandomFlashcard(flashcardRepository)
        randomGet =
                getRandomFlashcard.RandomFlashcardRepoGet("Doesn't Matter",
                        object : UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue> {
                            override fun onSuccess(response: GetRandomFlashcard.ResponseValue) {
                                /* Doesn't matter */
                            }

                            override fun onError() { /* Doesn't matter */
                            }
                        })
    }

    @Test
    fun `when only one flashcard and null previous flashcard, gets flashcard and calls success on callback`() {
        // Request value represents the previous Flashcard
        // In this null case, there was no previous Flashcard
        useCaseHandler.execute(getRandomFlashcard, requestValuesNull, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(SINGLE_FLASHCARD_LIST)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_1, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `when only one flashcard and same previous flashcard, gets flashcard and calls success on callback`() {
        // This test is to make sure it doesn't run into an infinite loop of trying to find
        // a different Flashcard from the previous one when there is only one to be had.
        useCaseHandler.execute(getRandomFlashcard, requestValuesFlashcard1, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(SINGLE_FLASHCARD_LIST)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_1, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `when all flashcards have same id, gets first flashcard in list and calls success on callback`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesFlashcard1, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST_SAME_IDS)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_1, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `gets flashcard different from previous and calls success on callback`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesFlashcard1, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_2, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `when data not available, calls failure on callback`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesNull, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }

    @Test
    fun `when no flashcards from repository, calls failure on callback`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesNull, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        // Repository replies with empty list
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(listOf())
        verify(callback).onError()
    }

    @Test
    fun `get with category, null previous id, gets flashcard from that category`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesCategory, callback)
        verify(flashcardRepository).getFlashcardsByCategoryName(
                eq(requestValuesCategory.categoryName!!), repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST_OF_CATEGORY_1)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(requestValuesCategory.categoryName,
                responseCaptor.firstValue.flashcard.category)
    }

    @Test
    fun `get with category and previous id, returns not previous id flashcard`() {
        useCaseHandler.execute(getRandomFlashcard, requestValuesCategoryAndPrevious, callback)
        verify(flashcardRepository).getFlashcardsByCategoryName(
                eq(requestValuesCategoryAndPrevious.categoryName!!),
                repositoryCallbackCaptor.capture())
        val notPreviousCard = Flashcard("Some id", CATEGORY_1.name, "An afront", "Backy")
        val listOfCategoryOne = listOf(FLASHCARD_1, notPreviousCard)
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(listOfCategoryOne)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(notPreviousCard, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun `index selection never goes out of list size bounds`() {
        val listSize = 100
        for (i in 0..1_000_000) {
            val index = randomGet.getRandomDistributedSelection(listSize)
            assertTrue(index < listSize)
            assertTrue(index >= 0)
        }
    }

    @Test
    fun `sort flashcards sorts by priority`() {
        val flashcard1 = Flashcard("0", "Whatever", "Front", "Back",
                2.5f)
        val flashcard2 = Flashcard("1", "Whatever", "Front", "Back",
                2.4f)
        val flashcard3 = Flashcard("2", "Whatever", "Front", "Back",
                2.6f)
        val flashcard4 = Flashcard("3", "Whatever", "Front", "Back",
                2.1f)
        val unsortedFlashcards = listOf(flashcard1, flashcard2, flashcard3, flashcard4)
        val sortedFlashcards = listOf(flashcard4, flashcard2, flashcard1, flashcard3)
        assertEquals(sortedFlashcards, randomGet.sortFlashcards(unsortedFlashcards))
    }
}
