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

package com.gregspitz.flashcardappkotlin.randomflashcard

import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder

/**
 * Tests for the implementation of {@link RandomFlashcardPresenter}
 */
class RandomFlashcardPresenterTest {

    private val response1 = GetRandomFlashcard.ResponseValue(FLASHCARD_1)

    private val response2 = GetRandomFlashcard.ResponseValue(FLASHCARD_2)

    private val getRandomFlashcard: GetRandomFlashcard = mock()

    private val useCaseHandler: UseCaseHandler = mock()

    private val randomFlashcardView: RandomFlashcardContract.View = mock()

    private val randomFlashcardViewModel: RandomFlashcardContract.ViewModel = mock()

    private val useCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue>>()

    private val requestCaptor = argumentCaptor<GetRandomFlashcard.RequestValues>()

    private val flashcardCaptor = argumentCaptor<Flashcard>()

    private lateinit var presenter: RandomFlashcardPresenter

    @Before
    fun setup() {
        whenever(randomFlashcardView.isActive()).thenReturn(true)
    }

    @Test
    fun creation_setsPresenterOnView() {
        presenter = createPresenter()
        verify(randomFlashcardView).setPresenter(presenter)
    }

    @Test
    fun onStart_showsFrontOfOnlyFlashcardIfThereIsOnlyOne() {
        createAndStartPresenter()
        val inOrder = verifyLoadingIndicatorInOrderStart()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onSuccess(response1)
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)
        verify(randomFlashcardViewModel).setFlashcard(FLASHCARD_1)
        verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
    }

    @Test
    fun onNewFlashcard_loadsDifferentFlashcard() {
        createAndStartPresenter()
        val inOrder = verifyLoadingIndicatorInOrderStart()
        val useCaseHandlerInOrder = inOrder(useCaseHandler)
        useCaseHandlerInOrder.verify(useCaseHandler).execute(eq(getRandomFlashcard),
                requestCaptor.capture(), useCaseCallbackCaptor.capture())

        assertNull(requestCaptor.firstValue.flashcardId)

        useCaseCallbackCaptor.firstValue.onSuccess(response1)
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)

        presenter.loadNewFlashcard()
        inOrder.verify(randomFlashcardView).setLoadingIndicator(true)
        useCaseHandlerInOrder.verify(useCaseHandler).execute(eq(getRandomFlashcard),
                requestCaptor.capture(), useCaseCallbackCaptor.capture())

        assertEquals(FLASHCARD_1.id, requestCaptor.secondValue.flashcardId)

        useCaseCallbackCaptor.secondValue.onSuccess(response2)
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)

        verify(randomFlashcardViewModel, times(2))
                .setFlashcard(flashcardCaptor.capture())
        val firstFlashcardShown = flashcardCaptor.firstValue
        val secondFlashcardShown = flashcardCaptor.secondValue
        assertNotEquals(firstFlashcardShown, secondFlashcardShown)
    }


    @Test
    fun turnFlashcardFromFront_showsBackInView() {
        createStartAndGetFlashcard()
        whenever(randomFlashcardViewModel.getFlashcardSide()).thenReturn(FlashcardSide.FRONT)
        val inOrder = inOrder(randomFlashcardViewModel)
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
        presenter.turnFlashcard()
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.BACK)
    }

    @Test
    fun turnFlashcardFromBack_showsFrontInView() {
        createStartAndGetFlashcard()
        whenever(randomFlashcardViewModel.getFlashcardSide()).thenReturn(FlashcardSide.FRONT)
        val inOrder = inOrder(randomFlashcardViewModel)
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
        presenter.turnFlashcard()
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.BACK)
        whenever(randomFlashcardViewModel.getFlashcardSide()).thenReturn(FlashcardSide.BACK)
        presenter.turnFlashcard()
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
    }

    @Test
    fun onDataNotAvailable_showsFailedToLoadFlashcardInView() {
        createAndStartPresenter()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onError()
        verify(randomFlashcardView).showFailedToLoadFlashcard()
    }

    @Test
    fun viewNotActive_turnFlashcard_doesNotCallViewModel() {
        // TODO: maybe even when the view is inactive, the presenter should change the viewmodel
        // in that case change this text and the next couple
        whenever(randomFlashcardView.isActive()).thenReturn(false)
        createStartAndGetFlashcard()
        presenter.turnFlashcard()
        verify(randomFlashcardView, never()).setLoadingIndicator(any())
        verify(randomFlashcardViewModel, never()).setFlashcardSide(any())
    }

    @Test
    fun viewNotActive_loadNewFlashcardOnSuccess_doesNotCallViewOrViewModel() {
        whenever(randomFlashcardView.isActive()).thenReturn(false)
        createStartAndGetFlashcard()
        verify(randomFlashcardView, never()).setLoadingIndicator(any())
        verify(randomFlashcardViewModel, never()).setFlashcard(any())
        verify(randomFlashcardViewModel, never()).setFlashcardSide(any())
    }

    @Test
    fun viewNotActive_loadNewFlashcardOnError_doesNotCallView() {
        whenever(randomFlashcardView.isActive()).thenReturn(false)
        createAndStartPresenter()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onError()
        verify(randomFlashcardView, never()).setLoadingIndicator(any())
        verify(randomFlashcardView, never()).showFailedToLoadFlashcard()
    }

    private fun createStartAndGetFlashcard() {
        createAndStartPresenter()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onSuccess(response1)
    }

    private fun verifyLoadingIndicatorInOrderStart() : InOrder {
        val inOrder = inOrder(randomFlashcardView)
        inOrder.verify(randomFlashcardView).setLoadingIndicator(true)
        return inOrder
    }

    private fun createAndStartPresenter() {
        presenter = createPresenter()
        presenter.start()
    }

    private fun createPresenter(): RandomFlashcardPresenter {
        return RandomFlashcardPresenter(useCaseHandler, randomFlashcardView,
                randomFlashcardViewModel, getRandomFlashcard)
    }
}
