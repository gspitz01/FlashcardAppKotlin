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
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST_OF_CATEGORY_1
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.DeleteFlashcards
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

    private val deleteFlashcards: DeleteFlashcards = mock()
    private val deleteFlashcardsRequestCaptor =
            argumentCaptor<DeleteFlashcards.RequestValues>()

    private val useCaseHandler: UseCaseHandler = mock()

    private val flashcardListView : FlashcardListContract.View = mock()

    // InOrder for verifying setLoadingIndicator on view
    private val inOrder = inOrder(flashcardListView)

    private val flashcardListViewModel: FlashcardListContract.ViewModel = mock()

    private val getUseCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetFlashcards.ResponseValue>>()
    private val deleteUseCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<DeleteFlashcards.ResponseValue>>()

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
                getUseCaseCallbackCaptor.capture())
        assertNull(getFlashcardsRequestCaptor.firstValue.categoryName)
        val response = GetFlashcards.ResponseValue(FLASHCARD_LIST)
        getUseCaseCallbackCaptor.firstValue.onSuccess(response)
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
                getUseCaseCallbackCaptor.capture())
        assertEquals(CATEGORY_1.name, getFlashcardsRequestCaptor.firstValue.categoryName)
        val response =
                GetFlashcards.ResponseValue(FLASHCARD_LIST_OF_CATEGORY_1)
        getUseCaseCallbackCaptor.firstValue.onSuccess(response)
        verifySetLoadingIndicator(false)
        verify(flashcardListViewModel).setFlashcards(FLASHCARD_LIST_OF_CATEGORY_1)
    }

    @Test
    fun `when error from use case, shows failed to load in view`() {
        createAndStartPresenter()
        verify(useCaseHandler).execute(eq(getFlashcards), any(), getUseCaseCallbackCaptor.capture())
        getUseCaseCallbackCaptor.firstValue.onError()
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

    @Test
    fun `on delete all flashcards, deletes all flashcards, shows success on view, shows add flashcard view`() {
        createAndStartPresenter()
        flashcardListPresenter.deleteAllFlashcards()

        captureDeleteUseCaseExecution()
        assertNull(deleteFlashcardsRequestCaptor.firstValue.categoryName)
        val response = DeleteFlashcards.ResponseValue()
        deleteUseCaseCallbackCaptor.firstValue.onSuccess(response)

        verify(flashcardListView).showDeleteSuccess()
        verify(flashcardListView).showAddFlashcard()
    }

    @Test
    fun `on delete all flashcards, failure from use case, calls delete failed on view`() {
        createAndStartPresenter()
        flashcardListPresenter.deleteAllFlashcards()

        captureDeleteUseCaseExecution()
        assertNull(deleteFlashcardsRequestCaptor.firstValue.categoryName)
        deleteUseCaseCallbackCaptor.firstValue.onError()

        verify(flashcardListView).showDeleteFailed()
    }

    @Test
    fun `on delete by category, deletes by category on use case, shows success on view, shows category list view`() {
        createAndStartPresenter()
        flashcardListPresenter.deleteFlashcardsFromCategory(FLASHCARD_1.category)

        captureDeleteUseCaseExecution()
        assertEquals(FLASHCARD_1.category, deleteFlashcardsRequestCaptor.firstValue.categoryName)
        val response = DeleteFlashcards.ResponseValue()
        deleteUseCaseCallbackCaptor.firstValue.onSuccess(response)

        verify(flashcardListView).showDeleteSuccess()
        verify(flashcardListView).showCategoryList()
    }

    @Test
    fun `on delete by category, failure from use case, calls delete failed on view`() {
        createAndStartPresenter()
        flashcardListPresenter.deleteFlashcardsFromCategory(FLASHCARD_1.category)

        captureDeleteUseCaseExecution()
        assertEquals(FLASHCARD_1.category, deleteFlashcardsRequestCaptor.firstValue.categoryName)
        deleteUseCaseCallbackCaptor.firstValue.onError()

        verify(flashcardListView).showDeleteFailed()
    }

    private fun createAndStartPresenter() {
        flashcardListPresenter = createPresenter()
        flashcardListPresenter.start()
    }

    private fun createPresenter(): FlashcardListPresenter {
        return FlashcardListPresenter(useCaseHandler, flashcardListView, flashcardListViewModel,
                getFlashcards, deleteFlashcards)
    }

    private fun verifySetLoadingIndicator(active: Boolean) {
        inOrder.verify(flashcardListView).setLoadingIndicator(active)
    }

    private fun captureDeleteUseCaseExecution() {
        verify(useCaseHandler).execute(eq(deleteFlashcards), deleteFlashcardsRequestCaptor.capture(),
                deleteUseCaseCallbackCaptor.capture())
    }
}
