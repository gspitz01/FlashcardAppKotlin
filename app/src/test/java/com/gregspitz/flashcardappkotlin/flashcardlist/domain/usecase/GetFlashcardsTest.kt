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

import com.gregspitz.flashcardappkotlin.TestData
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST_WITH_CATEGORIES
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link GetFlashcards}
 */
class GetFlashcardsTest {

    private val values = GetFlashcards.RequestValues()

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
        useCaseHandler.execute(getFlashcards, values, callback)
        verify(flashcardRepository).getFlashcards(repositoryCallbackCaptor.capture())
    }

    @Test
    fun `gets flashcards from repository and on success calls success on callback`() {
        repositoryCallbackCaptor.firstValue.onFlashcardsLoaded(TestData.FLASHCARD_LIST)
        verify(callback).onSuccess(responseCaptor.capture())
        assertEquals(FLASHCARD_LIST_WITH_CATEGORIES, responseCaptor.firstValue.flashcards)
    }

    @Test
    fun `gets flashcards from repository and on failure calls fail on callback`() {
        repositoryCallbackCaptor.firstValue.onDataNotAvailable()
        verify(callback).onError()
    }
}
