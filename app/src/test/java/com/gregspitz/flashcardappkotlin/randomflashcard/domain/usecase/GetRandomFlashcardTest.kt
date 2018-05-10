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

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link GetRandomFlashcard}
 */
class GetRandomFlashcardTest {

    private val flashcard1 = Flashcard("0", "Front", "Back")
    private val flashcard2 = Flashcard("1", "Front", "Back")
    private val singleFlashcardList = listOf(flashcard1)
    private val flashcards = listOf(flashcard1, flashcard2)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

    private val callback: UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue> = mock()

    private val responseCaptor = argumentCaptor<GetRandomFlashcard.ResponseValue>()

    private lateinit var getRandomFlashcards: GetRandomFlashcard

    @Before
    fun setup() {
        getRandomFlashcards = GetRandomFlashcard(flashcardRepository)
    }

    @Test
    fun onlyOneFlashcard_nullPreviousFlashcard_getsFlashcardAndCallsSuccessOnCallback() {
        val values = GetRandomFlashcard.RequestValues(null)
        useCaseHandler.execute(getRandomFlashcards, values, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(singleFlashcardList)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(flashcard1, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun onlyOneFlashcard_samePreviousFlashcard_getsFlashcardAndCallsSuccessOnCallback() {
        val values = GetRandomFlashcard.RequestValues(flashcard1.id)
        useCaseHandler.execute(getRandomFlashcards, values, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(singleFlashcardList)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(flashcard1, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun bothFlashcards_getsFlashcardDifferentFromPreviousAndCallsSuccessOnCallback() {
        val values = GetRandomFlashcard.RequestValues(flashcard1.id)
        useCaseHandler.execute(getRandomFlashcards, values, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(flashcards)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(flashcard2, responseCaptor.firstValue.flashcard)
    }

    @Test
    fun dataNotAvailable_callsFailureOnCallback() {
        val values = GetRandomFlashcard.RequestValues(null)
        useCaseHandler.execute(getRandomFlashcards, values, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }

    @Test
    fun noFlashcardsFromRepository_callsFailureOnCallback() {
        val values = GetRandomFlashcard.RequestValues(null)
        useCaseHandler.execute(getRandomFlashcards, values, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(listOf())
        verify(callback).onError()
    }
}
