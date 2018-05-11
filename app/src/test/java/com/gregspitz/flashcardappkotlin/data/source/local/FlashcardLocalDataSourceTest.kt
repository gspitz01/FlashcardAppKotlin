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

package com.gregspitz.flashcardappkotlin.data.source.local

import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.nhaarman.mockito_kotlin.*
import org.junit.Test

/**
 * Tests for {@link FlashcardLocalDataSource}
 */
class FlashcardLocalDataSourceTest {

    private val flashcard1 = Flashcard("0", "Front", "Back")
    private val flashcard2 = Flashcard("1", "Front", "Back")
    private val flashcards = listOf(flashcard1, flashcard2)

    private val mockFlashcardDao: FlashcardDao = mock()

    private val flashcardLocalDataSource = FlashcardLocalDataSource(mockFlashcardDao)

    @Test
    fun getFlashcards_getsFlashcardsFromFlashcardDaoAndPassesThemToCallback() {
        whenever(mockFlashcardDao.getFlashcards()).thenReturn(flashcards)
        val getFlashcardsCallback: FlashcardDataSource.GetFlashcardsCallback = mock()
        flashcardLocalDataSource.getFlashcards(getFlashcardsCallback)
        verify(getFlashcardsCallback).onFlashcardsLoaded(eq(flashcards))
    }

    @Test
    fun getFlashcardWithAvailableFlashcard_getsFromDaoAndPassesToCallback() {
        whenever(mockFlashcardDao.getFlashcard(eq(flashcard1.id))).thenReturn(flashcard1)
        val getFlashcardCallback: FlashcardDataSource.GetFlashcardCallback = mock()
        flashcardLocalDataSource.getFlashcard(flashcard1.id, getFlashcardCallback)
        verify(getFlashcardCallback).onFlashcardLoaded(eq(flashcard1))
    }

    @Test
    fun getFlashcardWithUnavailableFlashcard_callsOnDataNotAvailableOnCallback() {
        whenever(mockFlashcardDao.getFlashcard(flashcard1.id)).thenReturn(null)
        val getFlashcardCallback: FlashcardDataSource.GetFlashcardCallback = mock()
        flashcardLocalDataSource.getFlashcard(flashcard1.id, getFlashcardCallback)
        verify(getFlashcardCallback).onDataNotAvailable()
    }

    @Test
    fun saveFlashcard_insertsFlashcardIntoDaoAndCallSaveSuccessfulOnCallback() {
        val saveFlashcardCallback: FlashcardDataSource.SaveFlashcardCallback = mock()
        flashcardLocalDataSource.saveFlashcard(flashcard1, saveFlashcardCallback)
        verify(mockFlashcardDao).insertFlashcard(eq(flashcard1))
        verify(saveFlashcardCallback).onSaveSuccessful()
    }

    @Test
    fun deleteAllFlashcards_deletesAllFlashcardsOnDao() {
        flashcardLocalDataSource.deleteAllFlashcards()
        verify(mockFlashcardDao).deleteFlashcards()
    }

    @Test
    fun refreshFlashcards_doesNothing() {
        flashcardLocalDataSource.refreshFlashcards()
        verifyZeroInteractions(mockFlashcardDao)
    }
}
