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

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST_OF_CATEGORY_1
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Tests for the implementation of {@link FlashcardListPresenter}
 */
class FlashcardListPresenterTest {

    private val getFlashcards: GetFlashcards = mock()
    private val getFlashcardsRequestCaptor =
            argumentCaptor<GetFlashcards.RequestValues>()

    private val useCaseHandler: UseCaseHandler = mock()

    private val flashcardListView : FlashcardListContract.View = mock()

    // InOrder for verifying setLoadingIndicator on view
    private val inOrder = inOrder(flashcardListView)

    private val flashcardListViewModel: FlashcardListContract.ViewModel = mock()

    private val useCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetFlashcards.ResponseValue>>()

    private lateinit var flashcardListPresenter: FlashcardListPresenter

    @Before
    fun setup() {
        whenever(flashcardListView.isActive()).thenReturn(true)
        whenever(flashcardListView.getCategoryName()).thenReturn(null)
    }

    @Test
    fun `creation sets presenter on view`() {
        flashcardListPresenter = createPresenter()
        verify(flashcardListView).setPresenter(flashcardListPresenter)
    }

    @Test
    fun `startup without category shows flashcard list in view`() {
        createAndStartPresenter()
        verifySetLoadingIndicator(true)
        verify(flashcardListView).getCategoryName()
        verify(useCaseHandler).execute(eq(getFlashcards), getFlashcardsRequestCaptor.capture(),
                useCaseCallbackCaptor.capture())
        assertNull(getFlashcardsRequestCaptor.firstValue.categoryName)
        val response = GetFlashcards.ResponseValue(FLASHCARD_LIST)
        useCaseCallbackCaptor.firstValue.onSuccess(response)
        verifySetLoadingIndicator(false)
        verify(flashcardListViewModel).setFlashcards(FLASHCARD_LIST)
    }

    @Test
    fun `startup with category shows flashcard list of that category in view`() {
        whenever(flashcardListView.getCategoryName()).thenReturn(CATEGORY_1.name)
        createAndStartPresenter()
        verifySetLoadingIndicator(true)
        verify(flashcardListView).getCategoryName()
        verify(useCaseHandler).execute(eq(getFlashcards), getFlashcardsRequestCaptor.capture(),
                useCaseCallbackCaptor.capture())
        assertEquals(CATEGORY_1.name, getFlashcardsRequestCaptor.firstValue.categoryName)
        val response =
                GetFlashcards.ResponseValue(FLASHCARD_LIST_OF_CATEGORY_1)
        useCaseCallbackCaptor.firstValue.onSuccess(response)
        verifySetLoadingIndicator(false)
        verify(flashcardListViewModel).setFlashcards(FLASHCARD_LIST_OF_CATEGORY_1)
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

    @Test
    fun `on category click shows list of flashcards for that category`() {
        createAndStartPresenter()
        flashcardListPresenter.onCategoryClick(0)
        verify(flashcardListView).showCategoryFlashcardList(0)
    }

    private fun createAndStartPresenter() {
        flashcardListPresenter = createPresenter()
        flashcardListPresenter.start()
    }

    private fun createPresenter(): FlashcardListPresenter {
        return FlashcardListPresenter(useCaseHandler, flashcardListView, flashcardListViewModel,
                getFlashcards)
    }

    private fun verifySetLoadingIndicator(active: Boolean) {
        inOrder.verify(flashcardListView).setLoadingIndicator(active)
    }
}
