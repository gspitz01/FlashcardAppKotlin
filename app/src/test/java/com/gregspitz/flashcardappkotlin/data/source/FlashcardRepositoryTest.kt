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

package com.gregspitz.flashcardappkotlin.data.source

import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

/**
 * Tests for {@link FlashcardRepository}
 */
class FlashcardRepositoryTest {

    private val mockLocalDataSource: FlashcardDataSource = mock()

    private val flashcardRepository = FlashcardRepository(mockLocalDataSource)

    @Test
    fun getFlashcards_callsGetFlashcardsOnLocalDataSource() {
        val getFlashcardsCallback: FlashcardDataSource.GetFlashcardsCallback = mock()
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(mockLocalDataSource).getFlashcards(eq(getFlashcardsCallback))
    }

    @Test
    fun getFlashcard_callsGetFlashcardOnLocalDataSource() {
        val getFlashcardCallback: FlashcardDataSource.GetFlashcardCallback = mock()
        val flashcardId = "id"
        flashcardRepository.getFlashcard(flashcardId, getFlashcardCallback)
        verify(mockLocalDataSource).getFlashcard(eq(flashcardId), eq(getFlashcardCallback))
    }

    @Test
    fun saveFlashcard_callsSaveFlashcardOnLocalDataSource() {
        val saveFlashcardCallback: FlashcardDataSource.SaveFlashcardCallback = mock()
        val flashcard = Flashcard("0", "Front", "Back")
        flashcardRepository.saveFlashcard(flashcard, saveFlashcardCallback)
        verify(mockLocalDataSource).saveFlashcard(eq(flashcard), eq(saveFlashcardCallback))
    }

    @Test
    fun deleteAllFlashcards_callsDeleteAllFlashcardsOnLocalDataSource() {
        flashcardRepository.deleteAllFlashcards()
        verify(mockLocalDataSource).deleteAllFlashcards()
    }

    @Test(expected = NotImplementedError::class)
    fun refreshFlashcards_throwsNotImplementedError() {
        flashcardRepository.refreshFlashcards()
    }
}
