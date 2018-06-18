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

package com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for {@link SaveFlashcard}
 */
class SaveFlashcardTest {

    private val values = SaveFlashcard.RequestValues(FLASHCARD_1)

    private val flashcardRepository: FlashcardRepository = mock()

    private val useCaseHandler = UseCaseHandler(TestUseCaseScheduler())

    private val repositoryCallbackCaptor =
            argumentCaptor<FlashcardDataSource.SaveFlashcardCallback>()

    private val callback: UseCase.UseCaseCallback<SaveFlashcard.ResponseValue> = mock()

    private lateinit var saveFlashcard: SaveFlashcard

    @Before
    fun setup() {
        saveFlashcard = SaveFlashcard(flashcardRepository)
        useCaseHandler.execute(saveFlashcard, values, callback)
        verify(flashcardRepository).saveFlashcard(eq(FLASHCARD_1), repositoryCallbackCaptor.capture())
    }

    @Test
    fun `on success from repository, calls success on callback`() {
        repositoryCallbackCaptor.firstValue.onSaveSuccessful()
        verify(callback).onSuccess(any())
    }

    @Test
    fun `on failure from repository, calls error on callback`() {
        repositoryCallbackCaptor.firstValue.onSaveFailed()
        verify(callback).onError()
    }
}
