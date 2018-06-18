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

package com.gregspitz.flashcardappkotlin.addeditflashcard

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.DeleteFlashcard
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.FlashcardListFragment
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder

/**
 * Tests for the implementation of {@link AddEditFlashcardPresenter}
 */
class AddEditFlashcardPresenterTest {

    private val response = GetFlashcard.ResponseValue(FLASHCARD_1)

    private val view: AddEditFlashcardContract.View = mock()

    // InOrder for view's setLoadingIndicator method
    private val inOrder = inOrder(view)

    // GetFlashcard use case
    private val getFlashcard: GetFlashcard = mock()
    private val getRequestCaptor = argumentCaptor<GetFlashcard.RequestValues>()
    private val getFlashcardCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetFlashcard.ResponseValue>>()

    // SaveFlashcard use case
    private val saveFlashcard: SaveFlashcard = mock()
    private val saveRequestCaptor = argumentCaptor<SaveFlashcard.RequestValues>()
    private val saveFlashcardCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<SaveFlashcard.ResponseValue>>()

    // DeleteFlashcard use case
    private val deleteFlashcard: DeleteFlashcard = mock()
    private val deleteRequestCaptor = argumentCaptor<DeleteFlashcard.RequestValues>()
    private val deleteFlashcardCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<DeleteFlashcard.ResponseValue>>()

    private val useCaseHandler: UseCaseHandler = mock()

    private lateinit var presenter: AddEditFlashcardPresenter

    @Before
    fun setup() {
        whenever(view.isActive()).thenReturn(true)
        whenever(view.getIdFromArguments()).thenReturn(FLASHCARD_1.id)
    }

    @Test
    fun `creation sets self on view`() {
        presenter = createPresenter()
        verify(view).setPresenter(presenter)
    }

    @Test
    fun `on start, shows flashcard in view`() {
        createAndStartPresenter()
        verifySetViewLoadingIndicator(true)
        verifyGetCallbackSuccess()
        assertEquals(FLASHCARD_1.id, getRequestCaptor.firstValue.flashcardId)
        verifySetViewLoadingIndicator(false)
        verify(view).showFlashcard(FLASHCARD_1)
    }

    @Test
    fun `on error, shows failed to load in view`() {
        createAndStartPresenter()
        verifySetViewLoadingIndicator(true)
        verifyGetCallbackFailure()
        verifySetViewLoadingIndicator(false)
        verify(view).showFailedToLoadFlashcard()
    }

    @Test
    fun `on save flashcard, saves to repository and shows save success in view`() {
        createAndStartPresenter()
        val inOrder = inOrder(useCaseHandler)
        // First get the Flashcard
        inOrder.verify(useCaseHandler).execute(eq(getFlashcard), getRequestCaptor.capture(),
                getFlashcardCallbackCaptor.capture())
        getFlashcardCallbackCaptor.firstValue.onSuccess(response)

        // Save the Flashcard
        presenter.saveFlashcard(FLASHCARD_1)
        // Verify use case called
        inOrder.verify(useCaseHandler).execute(eq(saveFlashcard), saveRequestCaptor.capture(),
                saveFlashcardCallbackCaptor.capture())
        // Make sure save was called on the correct Flashcard
        assertEquals(FLASHCARD_1, saveRequestCaptor.firstValue.flashcard)
        val saveResponse = SaveFlashcard.ResponseValue()

        // Trigger successful save
        saveFlashcardCallbackCaptor.firstValue.onSuccess(saveResponse)
        verify(view).showSaveSuccessful()
    }

    @Test
    fun `on save failed, show save failed in view`() {
        createAndStartPresenter()
        val inOrder = inOrder(useCaseHandler)
        // Get the Flashcard
        inOrder.verify(useCaseHandler).execute(eq(getFlashcard), getRequestCaptor.capture(),
                getFlashcardCallbackCaptor.capture())
        getFlashcardCallbackCaptor.firstValue.onSuccess(response)

        // Save the Flashcard
        presenter.saveFlashcard(FLASHCARD_1)

        // Verify use case called
        inOrder.verify(useCaseHandler).execute(eq(saveFlashcard), saveRequestCaptor.capture(),
                saveFlashcardCallbackCaptor.capture())
        // Make sure save was called on the correct Flashcard
        assertEquals(FLASHCARD_1, saveRequestCaptor.firstValue.flashcard)

        // Trigger failed save
        saveFlashcardCallbackCaptor.firstValue.onError()
        verify(view).showSaveFailed()
    }

    @Test
    fun `on delete success, shows list view`() {
        createAndStartPresenter()
        // Get multiple Flashcards
        val inOrder = getFlashcardsWithUseCaseHandlerInOrder()

        // Delete the Flashcard
        presenter.deleteFlashcard(FLASHCARD_1.id)

        // Verify use case called
        inOrder.verify(useCaseHandler).execute(eq(deleteFlashcard), deleteRequestCaptor.capture(),
                deleteFlashcardCallbackCaptor.capture())

        // Verify correct Flashcard to be deleted
        assertEquals(FLASHCARD_1.id, deleteRequestCaptor.firstValue.flashcardId)
        val deleteResponse = DeleteFlashcard.ResponseValue()

        // Trigger successful delete
        deleteFlashcardCallbackCaptor.firstValue.onSuccess(deleteResponse)
        verify(view).showFlashcardList(FlashcardListFragment.noParticularFlashcardExtra)
    }

    @Test
    fun `on delete failed, shows delete failed in view`() {
        createAndStartPresenter()
        // Get Flashcards
        val inOrder = getFlashcardsWithUseCaseHandlerInOrder()

        // Delete the Flashcard
        presenter.deleteFlashcard(FLASHCARD_1.id)

        // Verify use case called
        inOrder.verify(useCaseHandler).execute(eq(deleteFlashcard), deleteRequestCaptor.capture(),
                deleteFlashcardCallbackCaptor.capture())
        // Verify correct Flashcard to be deleted
        assertEquals(FLASHCARD_1.id, deleteRequestCaptor.firstValue.flashcardId)

        // Trigger failed delete
        deleteFlashcardCallbackCaptor.firstValue.onError()
        verify(view).showDeleteFailed()
    }

    @Test
    fun `on show list, calls show list view on view`() {
        createAndStartPresenter()
        presenter.showList()
        verify(view).showFlashcardList(eq(FLASHCARD_1.id))
    }

    private fun getFlashcardsWithUseCaseHandlerInOrder(): InOrder {
        val inOrder = inOrder(useCaseHandler)
        inOrder.verify(useCaseHandler).execute(eq(getFlashcard), getRequestCaptor.capture(),
                getFlashcardCallbackCaptor.capture())
        getFlashcardCallbackCaptor.firstValue.onSuccess(response)
        return inOrder
    }

    private fun verifyGetCallbackSuccess() {
        verifyGetCallback()
        getFlashcardCallbackCaptor.firstValue.onSuccess(response)
    }

    private fun verifyGetCallbackFailure() {
        verifyGetCallback()
        getFlashcardCallbackCaptor.firstValue.onError()
    }

    private fun verifyGetCallback() {
        verify(useCaseHandler).execute(eq(getFlashcard), getRequestCaptor.capture(),
                getFlashcardCallbackCaptor.capture())
    }

    private fun verifySetViewLoadingIndicator(active: Boolean) {
        inOrder.verify(view).setLoadingIndicator(active)
    }

    private fun createAndStartPresenter() {
        presenter = createPresenter()
        presenter.start()
    }

    private fun createPresenter(): AddEditFlashcardPresenter {
        return AddEditFlashcardPresenter(useCaseHandler, view,
                getFlashcard, saveFlashcard, deleteFlashcard)
    }
}
