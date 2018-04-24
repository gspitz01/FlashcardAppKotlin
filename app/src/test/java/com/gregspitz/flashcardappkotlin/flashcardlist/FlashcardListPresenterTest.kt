package com.gregspitz.flashcardappkotlin.flashcardlist

import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.flashcardlist.domain.usecase.GetFlashcards
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * Tests for the implementation of {@link FlashcardListPresenter}
 */
class FlashcardListPresenterTest {

    private val flashcard1 = Flashcard("0", "A front", "A back")

    private val flashcard2 = Flashcard("1", "A different front", "A different back")

    private val getFlashcards: GetFlashcards = mock()

    private val useCaseHandler: UseCaseHandler = mock()

    private val flashcardListView : FlashcardListContract.View = mock()

    private val useCaseCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetFlashcards.ResponseValue>>()

    private lateinit var flashcardListPresenter: FlashcardListPresenter

    @Before
    fun setup() {
        whenever(flashcardListView.isActive()).thenReturn(true)
    }

    @Test
    fun creation_setsPresenterOnView() {
        flashcardListPresenter = createPresenter()
        verify(flashcardListView).setPresenter(flashcardListPresenter)
    }

    @Test
    fun startup_showsFlashcardListInView() {
        createAndStartPresenter()
        val inOrder = inOrder(flashcardListView)
        inOrder.verify(flashcardListView).setLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(getFlashcards), any(), useCaseCallbackCaptor.capture())
        val flashcards = getTestFlashcardsList()
        val response = GetFlashcards.ResponseValue(flashcards)
        useCaseCallbackCaptor.firstValue.onSuccess(response)
        inOrder.verify(flashcardListView).setLoadingIndicator(false)
        verify(flashcardListView).showFlashcards(flashcards)
    }

    @Test
    fun onError_showsFailedToLoadInView() {
        createAndStartPresenter()
        verify(useCaseHandler).execute(eq(getFlashcards), any(), useCaseCallbackCaptor.capture())
        useCaseCallbackCaptor.firstValue.onError()
        verify(flashcardListView).showFailedToLoadFlashcards()
    }

    @Test
    fun addFlashcard_showsAddFlashcard() {
        createAndStartPresenter()
        flashcardListPresenter.addFlashcard()
        verify(flashcardListView).showAddFlashcard()
    }

    @Test
    fun onFlashcardClick_showsFlashcardDetails() {
        createAndStartPresenter()
        flashcardListPresenter.onFlashcardClick(flashcard1.id)
        verify(flashcardListView).showFlashcardDetailsUi(flashcard1.id)
    }

    private fun getTestFlashcardsList(): List<Flashcard> {
        return Arrays.asList(flashcard1, flashcard2)
    }

    private fun createAndStartPresenter() {
        flashcardListPresenter = createPresenter()
        flashcardListPresenter.start()
    }

    private fun createPresenter(): FlashcardListPresenter {
        return FlashcardListPresenter(useCaseHandler, flashcardListView, getFlashcards)
    }
}
