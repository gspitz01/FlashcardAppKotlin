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

package com.gregspitz.flashcardappkotlin.flashcardlist

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for the implementation of {@link FlashcardListPresenter}
 */
class FlashcardListPresenterTest {

    private val getFlashcards: GetFlashcards = mock()

    private val useCaseHandler: UseCaseHandler = mock()

    private val flashcardListView : FlashcardListContract.View = mock()

    private val flashcardListViewModel: FlashcardListContract.ViewModel = mock()

    private val useCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetFlashcards.ResponseValue>>()

    private lateinit var flashcardListPresenter: FlashcardListPresenter

    @Before
    fun setup() {
        whenever(flashcardListView.isActive()).thenReturn(true)
    }

    @Test
    fun `creation sets presenter on view`() {
        flashcardListPresenter = createPresenter()
        verify(flashcardListView).setPresenter(flashcardListPresenter)
    }

    @Test
    fun `startup shows flashcard list in view`() {
        createAndStartPresenter()
        val inOrder = inOrder(flashcardListView)
        inOrder.verify(flashcardListView).setLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(getFlashcards), any(), useCaseCallbackCaptor.capture())
        val response = GetFlashcards.ResponseValue(FLASHCARD_LIST)
        useCaseCallbackCaptor.firstValue.onSuccess(response)
        inOrder.verify(flashcardListView).setLoadingIndicator(false)
        verify(flashcardListViewModel).setFlashcards(FLASHCARD_LIST)
    }

    @Test
    fun `when error from use case, shows failed to load in view`() {
        createAndStartPresenter()
        verify(useCaseHandler).execute(eq(getFlashcards), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onError()
        verify(flashcardListView).showFailedToLoadFlashcards()
    }

    @Test
    fun `add flashcard shows add flashcard view`() {
        createAndStartPresenter()
        flashcardListPresenter.addFlashcard()
        verify(flashcardListView).showAddFlashcard()
    }

    @Test
    fun `on flashcard click shows flashcard details`() {
        createAndStartPresenter()
        flashcardListPresenter.onFlashcardClick(0)
        verify(flashcardListView).showFlashcardDetailsUi(0)
    }

    private fun createAndStartPresenter() {
        flashcardListPresenter = createPresenter()
        flashcardListPresenter.start()
    }

    private fun createPresenter(): FlashcardListPresenter {
        return FlashcardListPresenter(useCaseHandler, flashcardListView, flashcardListViewModel,
                getFlashcards)
    }
}
