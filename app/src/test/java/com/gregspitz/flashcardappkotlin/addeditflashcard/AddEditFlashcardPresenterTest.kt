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

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.GetFlashcard
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for the implementation of {@link AddEditFlashcardPresenter}
 */
class AddEditFlashcardPresenterTest {

    private val flashcard = Flashcard("0", "Front", "Back")

    private val response = GetFlashcard.ResponseValue(flashcard)

    private val view: AddEditFlashcardContract.View = mock()

    private val getFlashcard: GetFlashcard = mock()

    private val saveFlashcard: SaveFlashcard = mock()

    private val useCaseHandler: UseCaseHandler = mock()

    private val getRequestCaptor = argumentCaptor<GetFlashcard.RequestValues>()

    private val saveRequestCaptor = argumentCaptor<SaveFlashcard.RequestValues>()

    private val getFlashcardCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetFlashcard.ResponseValue>>()

    private val saveFlashcardCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<SaveFlashcard.ResponseValue>>()

    private lateinit var presenter: AddEditFlashcardPresenter

    @Before
    fun setup() {
        whenever(view.isActive()).thenReturn(true)
        whenever(view.getIdFromIntent()).thenReturn(flashcard.id)
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
        assertEquals(flashcard.id, getRequestCaptor.firstValue.flashcardId)
        inOrder.verify(view).setLoadingIndicator(false)
        verify(view).showFlashcard(flashcard)
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
        presenter.saveFlashcard(flashcard)
        inOrder.verify(useCaseHandler).execute(eq(saveFlashcard), saveRequestCaptor.capture(),
                saveFlashcardCallbackCaptor.capture())
        assertEquals(flashcard, saveRequestCaptor.firstValue.flashcard)
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
        presenter.saveFlashcard(flashcard)
        inOrder.verify(useCaseHandler).execute(eq(saveFlashcard), saveRequestCaptor.capture(),
                saveFlashcardCallbackCaptor.capture())
        assertEquals(flashcard, saveRequestCaptor.firstValue.flashcard)
        saveFlashcardCallbackCaptor.firstValue.onError()
        verify(view).showSaveFailed()
    }

    @Test
    fun showList_callsShowListViewOnView() {
        presenter = createPresenter()
        presenter.showList()
        verify(view).showFlashcardList()
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
        return AddEditFlashcardPresenter(useCaseHandler, view, getFlashcard, saveFlashcard)
    }
}
