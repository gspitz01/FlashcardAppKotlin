package com.gregspitz.flashcardappkotlin.randomflashcard

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder

/**
 * Tests for the implementation of {@link RandomFlashcardPresenter}
 */
class RandomFlashcardPresenterTest {

    private val flashcard1 = Flashcard("0", "Front", "Back")

    private val response1 = GetRandomFlashcard.ResponseValue(flashcard1)

    private val flashcard2 = Flashcard("1", "An affront", "Taken aback")

    private val response2 = GetRandomFlashcard.ResponseValue(flashcard2)

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
        verify(randomFlashcardViewModel).setFlashcard(flashcard1)
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

        assertEquals(flashcard1.id, requestCaptor.secondValue.flashcardId)

        useCaseCallbackCaptor.secondValue.onSuccess(response2)
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)

        verify(randomFlashcardViewModel, times(2))
                .setFlashcard(flashcardCaptor.capture())
        val firstFlashcardShown = flashcardCaptor.firstValue
        val secondFlashcardShown = flashcardCaptor.secondValue
        assertNotEquals(firstFlashcardShown, secondFlashcardShown)
    }


    @Test
    fun turnFlashcard_showsBackInView() {
        createAndStartPresenter()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onSuccess(response1)
        whenever(randomFlashcardViewModel.getFlashcardSide()).thenReturn(FlashcardSide.FRONT)
        val inOrder = inOrder(randomFlashcardViewModel)
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
        presenter.turnFlashcard()
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.BACK)
    }

    @Test
    fun onDataNotAvailable_showsFailedToLoadFlashcardInView() {
        createAndStartPresenter()
        verify(useCaseHandler)
                .execute(eq(getRandomFlashcard), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onError()
        verify(randomFlashcardView).showFailedToLoadFlashcard()
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
