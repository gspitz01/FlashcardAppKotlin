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

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.local.FlashcardDao
import com.gregspitz.flashcardappkotlin.data.source.local.FlashcardLocalDataSource
import com.nhaarman.mockito_kotlin.*
import org.junit.Test

/**
 * Tests for {@link FlashcardRepository}
 */
class FlashcardRepositoryTest {

    private val mockFlashcardDao: FlashcardDao = mock()

    private val localDataSource = FlashcardLocalDataSource(mockFlashcardDao)

    private val spyLocalDataSource: FlashcardLocalDataSource = spy(localDataSource)

    private val getFlashcardsArgumentCaptor =
            argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

    private val getFlashcardsCallback: FlashcardDataSource.GetFlashcardsCallback = mock()

    private val flashcardRepository = FlashcardRepository(spyLocalDataSource)

    @Test
    fun getFlashcards_callsGetFlashcardsOnLocalDataSourceFirstTime() {
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(spyLocalDataSource).getFlashcards(getFlashcardsArgumentCaptor.capture())
        getFlashcardsArgumentCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST)
    }

    @Test
    fun getFlashcardsSecondTime_getsFlashcardsFromCache() {
        // First call will go to local data source
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(spyLocalDataSource).getFlashcards(getFlashcardsArgumentCaptor.capture())
        getFlashcardsArgumentCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST)

        // Second call will get from cache
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(getFlashcardsCallback, times(2)).onFlashcardsLoaded(FLASHCARD_LIST)
        verifyNoMoreInteractions(spyLocalDataSource)
    }

    @Test
    fun getFlashcard_callsGetFlashcardOnLocalDataSource() {
        val getFlashcardCallback: FlashcardDataSource.GetFlashcardCallback = mock()
        val flashcardId = "id"
        flashcardRepository.getFlashcard(flashcardId, getFlashcardCallback)
        verify(spyLocalDataSource).getFlashcard(eq(flashcardId), any())
    }

    @Test
    fun saveFlashcard_callsSaveFlashcardToLocalDataSourceAndSetsCacheDirty() {
        // Call getFlashcards to make sure cache is not dirty
        getLocalFlashcardsWithArgumentCaptor()

        val saveFlashcardCallback: FlashcardDataSource.SaveFlashcardCallback = mock()
        val flashcard = Flashcard("0", "Some Category", "Front", "Back")
        flashcardRepository.saveFlashcard(flashcard, saveFlashcardCallback)
        verify(spyLocalDataSource).saveFlashcard(eq(flashcard), eq(saveFlashcardCallback))

        // Prove cache is dirty again
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(spyLocalDataSource, times(2))
                .getFlashcards(any())
    }

    @Test
    fun deleteFlashcard_deletesFlashcardFromCacheAndCallsDeleteOnLocalDataSource() {
        // Call getFlashcards once to make sure cache is not dirty
        getLocalFlashcardsWithArgumentCaptor()

        val deleteFlashcardCallback: FlashcardDataSource.DeleteFlashcardCallback = mock()
        flashcardRepository.deleteFlashcard(FLASHCARD_1.id, deleteFlashcardCallback)
        verify(spyLocalDataSource).deleteFlashcard(eq(FLASHCARD_1.id), eq(deleteFlashcardCallback))

        // Prove tries to get FLASHCARD_1 from local source because not found in cache
        val getFlashcardCallback: FlashcardDataSource.GetFlashcardCallback = mock()
        flashcardRepository.getFlashcard(FLASHCARD_1.id, getFlashcardCallback)
        verify(spyLocalDataSource).getFlashcard(FLASHCARD_1.id, getFlashcardCallback)
    }

    @Test
    fun deleteAllFlashcards_callsDeleteAllFlashcardsOnLocalDataSourceAndSetsCacheDirty() {
        // Call getFlashcards once to make sure cache is not dirty
        getLocalFlashcardsWithArgumentCaptor()

        flashcardRepository.deleteAllFlashcards()
        verify(spyLocalDataSource).deleteAllFlashcards()

        // Prove cache is dirty again
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(spyLocalDataSource, times(2)).getFlashcards(any())
    }

    @Test
    fun refreshFlashcards_setsCacheDirty() {
        // Call getFlashcards once to make sure cache is not dirty
        getLocalFlashcardsWithArgumentCaptor()

        flashcardRepository.refreshFlashcards()

        // Prove cache is dirty again
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(spyLocalDataSource, times(2)).getFlashcards(any())
    }

    private fun getLocalFlashcardsWithArgumentCaptor() {
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(spyLocalDataSource).getFlashcards(getFlashcardsArgumentCaptor.capture())
        getFlashcardsArgumentCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST)
    }
}
