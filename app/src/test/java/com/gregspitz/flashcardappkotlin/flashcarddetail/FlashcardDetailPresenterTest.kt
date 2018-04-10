package com.gregspitz.flashcardappkotlin.flashcarddetail

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for the implementation of {@link FlashcardDetailPresenter}
 */
class FlashcardDetailPresenterTest {

    private val flashcard = Flashcard("0", "Front", "Back")

    private val flashcardRepository: FlashcardRepository = mock()

    private val flashcardDetailView: FlashcardDetailContract.View = mock()

    private val flashcardDetailViewModel: FlashcardDetailContract.ViewModel = mock()

    private val callbackCaptor = argumentCaptor<FlashcardDataSource.GetFlashcardCallback>()

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
        createAndStartPresenterAndSetCallbackCaptor()

        val inOrder = inOrder(flashcardDetailView)
        inOrder.verify(flashcardDetailView).setLoadingIndicator(true)
        // Trigger callback
        callbackCaptor.firstValue.onFlashcardLoaded(flashcard)

        inOrder.verify(flashcardDetailView).setLoadingIndicator(false)
        verify(flashcardDetailViewModel).setFlashcard(flashcard)
    }

    @Test
    fun noAvailableFlashcard_showsFailedToLoadFlashcardInView() {
        createAndStartPresenterAndSetCallbackCaptor()

        val inOrder = inOrder(flashcardDetailView)
        inOrder.verify(flashcardDetailView).setLoadingIndicator(true)
        // Trigger callback
        callbackCaptor.firstValue.onDataNotAvailable()

        inOrder.verify(flashcardDetailView).setLoadingIndicator(false)
        verify(flashcardDetailView).showFailedToLoadFlashcard()
    }

    @Test
    fun editFlashcard_tellsViewToShowEditFlashcardView() {
        createAndStartPresenterAndSetCallbackCaptor()
        callbackCaptor.firstValue.onFlashcardLoaded(flashcard)
        presenter.editFlashcard()
        verify(flashcardDetailView).showEditFlashcard(flashcard.id)
    }

    private fun createAndStartPresenterAndSetCallbackCaptor() {
        presenter = createPresenter()
        presenter.start()
        verify(flashcardRepository).getFlashcard(eq(flashcard.id), callbackCaptor.capture())
    }

    private fun createPresenter(): FlashcardDetailPresenter {
        return FlashcardDetailPresenter(UseCaseHandler(TestUseCaseScheduler()),
                flashcardDetailView, flashcardDetailViewModel, GetFlashcard(flashcardRepository))
    }
}
