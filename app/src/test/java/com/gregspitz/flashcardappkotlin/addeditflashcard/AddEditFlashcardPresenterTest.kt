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

    private val getFlashcard: GetFlashcard = mock()

    private val saveFlashcard: SaveFlashcard = mock()

    private val deleteFlashcard: DeleteFlashcard = mock()

    private val useCaseHandler: UseCaseHandler = mock()

    private val getRequestCaptor = argumentCaptor<GetFlashcard.RequestValues>()

    private val saveRequestCaptor = argumentCaptor<SaveFlashcard.RequestValues>()

    private val deleteRequestCaptor = argumentCaptor<DeleteFlashcard.RequestValues>()

    private val getFlashcardCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetFlashcard.ResponseValue>>()

    private val saveFlashcardCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<SaveFlashcard.ResponseValue>>()

    private val deleteFlashcardCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<DeleteFlashcard.ResponseValue>>()

    private lateinit var presenter: AddEditFlashcardPresenter

    @Before
    fun setup() {
        whenever(view.isActive()).thenReturn(true)
        whenever(view.getIdFromArguments()).thenReturn(FLASHCARD_1.id)
    }

    @Test
    fun creation_setsPresenterOnView() {
        presenter = createPresenter()
        verify(view).setPresenter(presenter)
    }

    @Test
    fun onStart_showsFlashcardInView() {
        createAndStartPresenter()
        val inOrder = inOrder(view)
        inOrder.verify(view).setLoadingIndicator(true)
        verifyGetCallbackSuccess()
        assertEquals(FLASHCARD_1.id, getRequestCaptor.firstValue.flashcardId)
        inOrder.verify(view).setLoadingIndicator(false)
        verify(view).showFlashcard(FLASHCARD_1)
    }

    @Test
    fun onError_showsFailedToLoadInView() {
        createAndStartPresenter()
        val inOrder = inOrder(view)
        inOrder.verify(view).setLoadingIndicator(true)
        verifyGetCallbackFailure()
        inOrder.verify(view).setLoadingIndicator(false)
        verify(view).showFailedToLoadFlashcard()
    }

    @Test
    fun saveFlashcard_savesToRepositoryAndShowsSaveSuccessInView() {
        createAndStartPresenter()
        val inOrder = inOrder(useCaseHandler)
        inOrder.verify(useCaseHandler).execute(eq(getFlashcard), getRequestCaptor.capture(),
                getFlashcardCallbackCaptor.capture())
        getFlashcardCallbackCaptor.firstValue.onSuccess(response)
        presenter.saveFlashcard(FLASHCARD_1)
        inOrder.verify(useCaseHandler).execute(eq(saveFlashcard), saveRequestCaptor.capture(),
                saveFlashcardCallbackCaptor.capture())
        assertEquals(FLASHCARD_1, saveRequestCaptor.firstValue.flashcard)
        val saveResponse = SaveFlashcard.ResponseValue()
        saveFlashcardCallbackCaptor.firstValue.onSuccess(saveResponse)
        verify(view).showSaveSuccessful()
    }

    @Test
    fun saveFailed_showSaveFailedInView() {
        createAndStartPresenter()
        val inOrder = inOrder(useCaseHandler)
        inOrder.verify(useCaseHandler).execute(eq(getFlashcard), getRequestCaptor.capture(),
                getFlashcardCallbackCaptor.capture())
        getFlashcardCallbackCaptor.firstValue.onSuccess(response)

        presenter.saveFlashcard(FLASHCARD_1)

        inOrder.verify(useCaseHandler).execute(eq(saveFlashcard), saveRequestCaptor.capture(),
                saveFlashcardCallbackCaptor.capture())
        assertEquals(FLASHCARD_1, saveRequestCaptor.firstValue.flashcard)
        saveFlashcardCallbackCaptor.firstValue.onError()
        verify(view).showSaveFailed()
    }

    @Test
    fun deleteSuccess_showsListViewInView() {
        createAndStartPresenter()
        val inOrder = getFlashcardsWithUseCaseHandlerInOrder()

        presenter.deleteFlashcard(FLASHCARD_1.id)

        inOrder.verify(useCaseHandler).execute(eq(deleteFlashcard), deleteRequestCaptor.capture(),
                deleteFlashcardCallbackCaptor.capture())
        assertEquals(FLASHCARD_1.id, deleteRequestCaptor.firstValue.flashcardId)
        val deleteResponse = DeleteFlashcard.ResponseValue()
        deleteFlashcardCallbackCaptor.firstValue.onSuccess(deleteResponse)
        verify(view).showFlashcardList(FlashcardListFragment.noParticularFlashcardExtra)
    }

    @Test
    fun deleteFailed_showsDeleteFailedInView() {
        createAndStartPresenter()
        val inOrder = getFlashcardsWithUseCaseHandlerInOrder()

        presenter.deleteFlashcard(FLASHCARD_1.id)

        inOrder.verify(useCaseHandler).execute(eq(deleteFlashcard), deleteRequestCaptor.capture(),
                deleteFlashcardCallbackCaptor.capture())
        assertEquals(FLASHCARD_1.id, deleteRequestCaptor.firstValue.flashcardId)
        deleteFlashcardCallbackCaptor.firstValue.onError()
        verify(view).showDeleteFailed()
    }

    @Test
    fun showList_callsShowListViewOnView() {
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

    private fun createAndStartPresenter() {
        presenter = createPresenter()
        presenter.start()
    }

    private fun createPresenter(): AddEditFlashcardPresenter {
        return AddEditFlashcardPresenter(useCaseHandler, view,
                getFlashcard, saveFlashcard, deleteFlashcard)
    }
}
