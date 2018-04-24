package com.gregspitz.flashcardappkotlin.flashcarddetail

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for the implementation of {@link FlashcardDetailPresenter}
 */
class FlashcardDetailPresenterTest {

    private val flashcard = Flashcard("0", "Front", "Back")

    private val response = GetFlashcard.ResponseValue(flashcard)

    private val getFlashcard: GetFlashcard = mock()

    private val useCaseHandler: UseCaseHandler = mock()

    private val flashcardDetailView: FlashcardDetailContract.View = mock()

    private val flashcardDetailViewModel: FlashcardDetailContract.ViewModel = mock()

    private val requestCaptor = argumentCaptor<GetFlashcard.RequestValues>()

    private val useCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetFlashcard.ResponseValue>>()

    private lateinit var presenter: FlashcardDetailPresenter

    @Before
    fun setup() {
        whenever(flashcardDetailView.isActive()).thenReturn(true)
        whenever(flashcardDetailView.getIdFromIntent()).thenReturn(flashcard.id)
    }

    @Test
    fun createPresenter_setsPresenterOnView() {
        presenter = createPresenter()
        verify(flashcardDetailView).setPresenter(presenter)
    }

    @Test
    fun loadFlashcard_showsFlashcardInView() {
        createAndStartPresenter()
        val inOrder = inOrder(flashcardDetailView)
        inOrder.verify(flashcardDetailView).setLoadingIndicator(true)
        verifyUseCaseCallbackSuccess()
        assertEquals(flashcard.id, requestCaptor.firstValue.flashcardId)
        inOrder.verify(flashcardDetailView).setLoadingIndicator(false)
        verify(flashcardDetailViewModel).setFlashcard(flashcard)
    }

    @Test
    fun onError_showsFailedToLoadFlashcardInView() {
        createAndStartPresenter()
        val inOrder = inOrder(flashcardDetailView)
        inOrder.verify(flashcardDetailView).setLoadingIndicator(true)
        verifyUseCaseCallbackFailure()
        inOrder.verify(flashcardDetailView).setLoadingIndicator(false)
        verify(flashcardDetailView).showFailedToLoadFlashcard()
    }

    @Test
    fun editFlashcard_tellsViewToShowEditFlashcardView() {
        createAndStartPresenter()
        verifyUseCaseCallbackSuccess()
        presenter.editFlashcard()
        verify(flashcardDetailView).showEditFlashcard(flashcard.id)
    }

    private fun verifyUseCaseCallbackSuccess() {
        verifyUseCaseCallback()
        useCaseCallbackCaptor.firstValue.onSuccess(response)
    }

    private fun verifyUseCaseCallbackFailure() {
        verifyUseCaseCallback()
        useCaseCallbackCaptor.firstValue.onError()
    }

    private fun verifyUseCaseCallback() {
        verify(useCaseHandler)
                .execute(eq(getFlashcard), requestCaptor.capture(), useCaseCallbackCaptor.capture())
    }

    private fun createAndStartPresenter() {
        presenter = createPresenter()
        presenter.start()
    }

    private fun createPresenter(): FlashcardDetailPresenter {
        return FlashcardDetailPresenter(useCaseHandler, flashcardDetailView,
                flashcardDetailViewModel, getFlashcard)
    }
}
