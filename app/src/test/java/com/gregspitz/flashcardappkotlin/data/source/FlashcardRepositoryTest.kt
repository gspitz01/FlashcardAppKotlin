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

    private val mockRemoteDataSource: FlashcardDataSource = mock()

    private val getFlashcardsArgumentCaptor =
            argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

    private val getFlashcardsCallback: FlashcardDataSource.GetFlashcardsCallback = mock()

    private val flashcardRepository = FlashcardRepository(spyLocalDataSource, mockRemoteDataSource)

    @Test
    fun getFlashcards_callsGetFlashcardsOnRemoteDataSourceFirstTime() {
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(mockRemoteDataSource).getFlashcards(getFlashcardsArgumentCaptor.capture())
        getFlashcardsArgumentCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST)

        // Also saves flashcards to local data source
        for (flashcard in FLASHCARD_LIST) {
            verify(spyLocalDataSource).saveFlashcard(eq(flashcard), any())
        }
    }

    @Test
    fun getFlashcardsSecondTime_getsFlashcardsFromCache() {
        // First call will go to remote data source
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(mockRemoteDataSource).getFlashcards(getFlashcardsArgumentCaptor.capture())
        getFlashcardsArgumentCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST)

        // Second call will get from cache
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(getFlashcardsCallback, times(2)).onFlashcardsLoaded(FLASHCARD_LIST)
        verifyNoMoreInteractions(mockRemoteDataSource)
    }

    @Test
    fun getFlashcards_remoteSourceFails_triesFromLocalSource() {
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(mockRemoteDataSource).getFlashcards(getFlashcardsArgumentCaptor.capture())
        getFlashcardsArgumentCaptor.firstValue.onDataNotAvailable()
        verify(spyLocalDataSource).getFlashcards(any())
    }

    @Test
    fun getFlashcard_callsGetFlashcardOnLocalDataSource() {
        val getFlashcardCallback: FlashcardDataSource.GetFlashcardCallback = mock()
        val flashcardId = "id"
        flashcardRepository.getFlashcard(flashcardId, getFlashcardCallback)
        verify(spyLocalDataSource).getFlashcard(eq(flashcardId), any())
    }

    @Test
    fun getFlashcard_failedLocalDataSource_callsGetFlashcardOnRemoteDataSource() {
        val getFlashcardCallback: FlashcardDataSource.GetFlashcardCallback = mock()
        val flashcardId = "id"
        whenever(mockFlashcardDao.getFlashcard(eq(flashcardId))).thenReturn(null)
        flashcardRepository.getFlashcard(flashcardId, getFlashcardCallback)
        verify(spyLocalDataSource).getFlashcard(eq(flashcardId), any())
        verify(mockRemoteDataSource).getFlashcard(eq(flashcardId), any())
    }

    @Test
    fun saveFlashcard_callsSaveFlashcardToRemoteDataSourceAndSetsCacheDirty() {
        // Call getFlashcards once to make sure cache is not dirty
        getRemoteFlashcardsWithArgumentCaptor()

        val saveFlashcardCallback: FlashcardDataSource.SaveFlashcardCallback = mock()
        val flashcard = Flashcard("0", "Some Category", "Front", "Back")
        flashcardRepository.saveFlashcard(flashcard, saveFlashcardCallback)
        verify(mockRemoteDataSource).saveFlashcard(eq(flashcard), eq(saveFlashcardCallback))

        // Prove cache is dirty again
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(mockRemoteDataSource, times(2))
                .getFlashcards(any())
    }

    @Test
    fun deleteAllFlashcards_callsDeleteAllFlashcardsOnLocalDataSourceAndSetsCacheDirty() {
        // Call getFlashcards once to make sure cache is not dirty
        getRemoteFlashcardsWithArgumentCaptor()

        flashcardRepository.deleteAllFlashcards()
        verify(spyLocalDataSource).deleteAllFlashcards()

        // Prove cache is dirty again
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(mockRemoteDataSource, times(2)).getFlashcards(any())
    }

    @Test
    fun refreshFlashcards_setsCacheDirty() {
        // Call getFlashcards once to make sure cache is not dirty
        getRemoteFlashcardsWithArgumentCaptor()

        flashcardRepository.refreshFlashcards()

        // Prove cache is dirty again
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(mockRemoteDataSource, times(2)).getFlashcards(any())
    }

    private fun getRemoteFlashcardsWithArgumentCaptor() {
        flashcardRepository.getFlashcards(getFlashcardsCallback)
        verify(mockRemoteDataSource).getFlashcards(getFlashcardsArgumentCaptor.capture())
        getFlashcardsArgumentCaptor.firstValue.onFlashcardsLoaded(FLASHCARD_LIST)
    }
}
