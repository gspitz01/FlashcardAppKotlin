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

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_1
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_2
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.categorylist.domain.usecase.GetCategories
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardPriority
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for the implementation of {@link RandomFlashcardPresenter}
 */
class RandomFlashcardPresenterTest {

    private val response1 = GetRandomFlashcard.ResponseValue(FLASHCARD_1)
    private val response2 = GetRandomFlashcard.ResponseValue(FLASHCARD_2)
    private val categoriesResponse =
            GetCategories.ResponseValue(listOf(CATEGORY_1))

    // GetRandomFlashcard use case
    private val getRandomFlashcard: GetRandomFlashcard = mock()
    private val getRandomFlashcardUseCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetRandomFlashcard.ResponseValue>>()
    private val getRandomFlashcardRequestCaptor =
            argumentCaptor<GetRandomFlashcard.RequestValues>()

    // GetCategories for the spinner
    private val getCategories: GetCategories = mock()
    private val getCategoriesUseCaseCallback =
            argumentCaptor<UseCase.UseCaseCallback<GetCategories.ResponseValue>>()

    // SaveFlashcard use case
    private val saveFlashcard: SaveFlashcard = mock()
    private val saveFlashcardUseCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<SaveFlashcard.ResponseValue>>()
    private val saveFlashcardRequestCaptor =
            argumentCaptor<SaveFlashcard.RequestValues>()

    private val useCaseHandler: UseCaseHandler = mock()

    private val randomFlashcardView: RandomFlashcardContract.View = mock()

    // InOrder to verify setLoadingIndicator on view
    private val inOrder = inOrder(randomFlashcardView)

    private val randomFlashcardViewModel: RandomFlashcardContract.ViewModel = mock()


    private val flashcardCaptor = argumentCaptor<Flashcard>()

    private lateinit var presenter: RandomFlashcardPresenter

    @Before
    fun setup() {
        whenever(randomFlashcardView.isActive()).thenReturn(true)
        whenever(randomFlashcardView.getCategoryName()).thenReturn(null)
    }

    @Test
    fun creation_setsPresenterOnView() {
        createPresenter()
        verify(randomFlashcardView).setPresenter(presenter)
    }

    @Test
    fun `on start, no category, shows front of only flashcard if there is only one, sets spinner categories`() {
        createAndStartPresenter()
        verifyLoadingIndicator(true)

        // calls getCategoryName on view
        verify(randomFlashcardView).getCategoryName()

        // GetRandomFlashcard use case is called
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(),
                        getRandomFlashcardUseCaseCallbackCaptor.capture())
        getRandomFlashcardUseCaseCallbackCaptor.firstValue.onSuccess(response1)
        verifyLoadingIndicator(false)

        // GetCategories use case called
        verify(useCaseHandler)
                .execute(eq(getCategories), any(), getCategoriesUseCaseCallback.capture())
        getCategoriesUseCaseCallback.firstValue.onSuccess(categoriesResponse)

        verify(randomFlashcardViewModel).setFlashcard(FLASHCARD_1)
        verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
        verify(randomFlashcardViewModel).setSpinnerCategories(categoriesResponse.categories)
    }

    @Test
    fun `on new flashcard, no category, loads different flashcard`() {
        createAndStartPresenter()
        verifyLoadingIndicator(true)
        val useCaseHandlerInOrder = inOrder(useCaseHandler)

        verify(randomFlashcardView).getCategoryName()

        // GetRandomFlashcard use case is called
        useCaseHandlerInOrder.verify(useCaseHandler).execute(eq(getRandomFlashcard),
                getRandomFlashcardRequestCaptor.capture(), getRandomFlashcardUseCaseCallbackCaptor.capture())

        // First time the presenter should send a request value with null for the flashcardId
        assertNull(getRandomFlashcardRequestCaptor.firstValue.flashcardId)
        // And null for categoryName
        assertNull(getRandomFlashcardRequestCaptor.firstValue.categoryName)

        // Use case responds successful with a Flashcard
        getRandomFlashcardUseCaseCallbackCaptor.firstValue.onSuccess(response1)
        verifyLoadingIndicator(false)

        presenter.loadNewFlashcard()
        verifyLoadingIndicator(true)

        verify(randomFlashcardView, times(2)).getCategoryName()
        useCaseHandlerInOrder.verify(useCaseHandler).execute(eq(getRandomFlashcard),
                getRandomFlashcardRequestCaptor.capture(), getRandomFlashcardUseCaseCallbackCaptor.capture())

        // Second call to GetRandomFlashcard, presenter sends the flashcardId of the
        // previously attained Flashcard
        assertEquals(FLASHCARD_1.id, getRandomFlashcardRequestCaptor.secondValue.flashcardId)
        // But categoryName should still be null
        assertNull(getRandomFlashcardRequestCaptor.secondValue.categoryName)

        getRandomFlashcardUseCaseCallbackCaptor.secondValue.onSuccess(response2)
        verifyLoadingIndicator(false)

        // ViewModel should have been called twice
        verify(randomFlashcardViewModel, times(2))
                .setFlashcard(flashcardCaptor.capture())
        val firstFlashcardShown = flashcardCaptor.firstValue
        val secondFlashcardShown = flashcardCaptor.secondValue
        // Two Flashcards should be different
        assertNotEquals(firstFlashcardShown, secondFlashcardShown)
    }

    @Test
    fun `on start with category, shows front of flashcard from that category, sets spinner categories`() {
        whenever(randomFlashcardView.getCategoryName()).thenReturn(CATEGORY_1.name)
        createAndStartPresenter()
        verifyLoadingIndicator(true)

        verify(randomFlashcardView).getCategoryName()

        verify(useCaseHandler).execute(eq(getRandomFlashcard),
                getRandomFlashcardRequestCaptor.capture(),
                getRandomFlashcardUseCaseCallbackCaptor.capture())

        // Verify use case called with correct category
        assertEquals(CATEGORY_1.name, getRandomFlashcardRequestCaptor.firstValue.categoryName)

        // Response1 needs to be a flashcard with Category1
        getRandomFlashcardUseCaseCallbackCaptor.firstValue.onSuccess(response1)

        // GetCategories use case called
        verify(useCaseHandler)
                .execute(eq(getCategories), any(), getCategoriesUseCaseCallback.capture())
        getCategoriesUseCaseCallback.firstValue.onSuccess(categoriesResponse)

        verify(randomFlashcardViewModel).setFlashcard(response1.flashcard)
        verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
        verify(randomFlashcardViewModel).setSpinnerCategories(categoriesResponse.categories)
    }


    @Test
    fun `turn flashcard from front shows back in viewmodel`() {
        createStartAndGetFlashcard()
        whenever(randomFlashcardViewModel.getFlashcardSide()).thenReturn(FlashcardSide.FRONT)
        val inOrder = inOrder(randomFlashcardViewModel)
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
        presenter.turnFlashcard()
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.BACK)
    }

    @Test
    fun `turn flashcard from back shows front in viewmodel`() {
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
    fun `on data not available shows failed to load flashcard in view`() {
        createAndStartPresenter()
        verify(randomFlashcardView).getCategoryName()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), getRandomFlashcardUseCaseCallbackCaptor.capture())
        getRandomFlashcardUseCaseCallbackCaptor.firstValue.onError()
        verify(randomFlashcardView).showFailedToLoadFlashcard()
    }

    @Test
    fun `on categories not available, calls show failed to load categories on view`() {
        createAndStartPresenter()
        verify(useCaseHandler)
                .execute(eq(getCategories), any(), getCategoriesUseCaseCallback.capture())
        getCategoriesUseCaseCallback.firstValue.onError()
        verify(randomFlashcardView).showFailedToLoadCategories()
    }

    @Test
    fun `on save flashcard, flashcard saved with new priority, success from use case, moves to next flashcard`() {
        createStartAndGetFlashcard()

        presenter.saveFlashcard(FlashcardPriority.URGENT)

        verifyCorrectSaveAttempt()

        // Success from use case
        saveFlashcardUseCaseCallbackCaptor.firstValue.onSuccess(SaveFlashcard.ResponseValue())
        verify(useCaseHandler, times(2)).execute(eq(getRandomFlashcard),
                getRandomFlashcardRequestCaptor.capture(),
                getRandomFlashcardUseCaseCallbackCaptor.capture())
        assertEquals(FLASHCARD_1.id, getRandomFlashcardRequestCaptor.secondValue.flashcardId)
    }

    @Test
    fun `on save flashcard, flashcard saved with new priority, failure from use case, moves to next flashcard`() {
        createStartAndGetFlashcard()

        presenter.saveFlashcard(FlashcardPriority.URGENT)

        verifyCorrectSaveAttempt()

        // Failure from use case
        saveFlashcardUseCaseCallbackCaptor.firstValue.onError()
        verify(useCaseHandler, times(2)).execute(eq(getRandomFlashcard),
                getRandomFlashcardRequestCaptor.capture(),
                getRandomFlashcardUseCaseCallbackCaptor.capture())
        assertEquals(FLASHCARD_1.id, getRandomFlashcardRequestCaptor.secondValue.flashcardId)
    }

    @Test
    fun `save attempt without having already retrieved a flashcard, does nothing`() {
        createPresenter()

        presenter.saveFlashcard(FlashcardPriority.URGENT)

        verifyNoMoreInteractions(useCaseHandler)
    }

    @Test
    fun `when view not active, turn flashcard does not call viewmodel`() {
        // TODO: maybe even when the view is inactive, the presenter should change the viewmodel
        // in that case change this text and the next couple
        whenever(randomFlashcardView.isActive()).thenReturn(false)
        createStartAndGetFlashcard()
        presenter.turnFlashcard()
        verify(randomFlashcardView, never()).setLoadingIndicator(any())
        verify(randomFlashcardViewModel, never()).setFlashcardSide(any())
    }

    @Test
    fun `when view not active, load new flashcard on success does not call view or viewmodel`() {
        whenever(randomFlashcardView.isActive()).thenReturn(false)
        createStartAndGetFlashcard()
        verify(randomFlashcardView, never()).setLoadingIndicator(any())
        verify(randomFlashcardViewModel, never()).setFlashcard(any())
        verify(randomFlashcardViewModel, never()).setFlashcardSide(any())
    }

    @Test
    fun `when view not active, load new flashcard on error does not call view`() {
        whenever(randomFlashcardView.isActive()).thenReturn(false)
        createAndStartPresenter()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), getRandomFlashcardUseCaseCallbackCaptor.capture())
        getRandomFlashcardUseCaseCallbackCaptor.firstValue.onError()
        verify(randomFlashcardView, never()).setLoadingIndicator(any())
        verify(randomFlashcardView, never()).showFailedToLoadFlashcard()
    }

    private fun verifyCorrectSaveAttempt() {
        verify(useCaseHandler).execute(eq(saveFlashcard), saveFlashcardRequestCaptor.capture(),
                saveFlashcardUseCaseCallbackCaptor.capture())

        // Saved flashcard should have same data as retrieved flashcard with new priority
        val expectedFlashcard = Flashcard(FLASHCARD_1.id, FLASHCARD_1.category, FLASHCARD_1.front,
                FLASHCARD_1.back, FlashcardPriority.URGENT)
        val actualFlashcard = saveFlashcardRequestCaptor.firstValue.flashcard
        assertEquals(expectedFlashcard.id, actualFlashcard.id)
        assertEquals(expectedFlashcard.front, actualFlashcard.front)
        assertEquals(expectedFlashcard.back, actualFlashcard.back)
        assertEquals(expectedFlashcard.category, actualFlashcard.category)
        assertEquals(expectedFlashcard.priority, actualFlashcard.priority)
    }

    private fun createStartAndGetFlashcard() {
        createAndStartPresenter()
        verify(randomFlashcardView).getCategoryName()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), getRandomFlashcardUseCaseCallbackCaptor.capture())
        getRandomFlashcardUseCaseCallbackCaptor.firstValue.onSuccess(response1)
    }

    private fun verifyLoadingIndicator(active: Boolean) {
        inOrder.verify(randomFlashcardView).setLoadingIndicator(active)
    }

    private fun createAndStartPresenter() {
        createPresenter()
        presenter.start()
    }

    private fun createPresenter() {
        presenter = RandomFlashcardPresenter(useCaseHandler, randomFlashcardView,
                randomFlashcardViewModel, getRandomFlashcard, getCategories, saveFlashcard)
    }
}
