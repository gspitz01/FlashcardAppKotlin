package com.gregspitz.flashcardappkotlin.randomflashcard

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.model.FlashcardSide
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.randomflashcard.domain.usecase.GetRandomFlashcard
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for the implementation of {@link RandomFlashcardPresenter}
 */
class RandomFlashcardPresenterTest {

    private val flashcard1 = Flashcard("0", "Front", "Back")

    private val flashcard2 = Flashcard("1", "An affront", "Taken aback")

    private val flashcardRepository: FlashcardRepository = mock()

    private val randomFlashcardView: RandomFlashcardContract.View = mock()

    private val randomFlashcardViewModel: RandomFlashcardContract.ViewModel = mock()

    private val callbackCaptor = argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

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
        createAndStartPresenterAndSetCallbackCaptor()
        val inOrder = inOrder(randomFlashcardView)
        inOrder.verify(randomFlashcardView).setLoadingIndicator(true)
        callbackCaptor.firstValue.onFlashcardsLoaded(getSingleFlashcardList())
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)
        verify(randomFlashcardViewModel).setFlashcard(flashcard1)
        verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
    }

    @Test
    fun onNewFlashcard_loadsDifferentFlashcardIfMultiple() {
        presenter = createPresenter()
        presenter.start()
        val inOrder = inOrder(randomFlashcardView)
        val repoInOrder = inOrder(flashcardRepository)
        repoInOrder.verify(flashcardRepository).getFlashcards(callbackCaptor.capture())
        inOrder.verify(randomFlashcardView).setLoadingIndicator(true)
        callbackCaptor.firstValue.onFlashcardsLoaded(getFlashcardList())
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)

        presenter.loadNewFlashcard()
        repoInOrder.verify(flashcardRepository).getFlashcards(callbackCaptor.capture())
        inOrder.verify(randomFlashcardView).setLoadingIndicator(true)
        callbackCaptor.secondValue.onFlashcardsLoaded(getFlashcardList())
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)

        verify(randomFlashcardViewModel, times(2))
                .setFlashcard(flashcardCaptor.capture())
        val firstFlashcardShown = flashcardCaptor.firstValue
        val secondFlashcardShown = flashcardCaptor.secondValue
        assertNotEquals(firstFlashcardShown, secondFlashcardShown)
    }

    @Test
    fun allFlashcardsHaveSameId_canRepeatFlashcardOnLoadNew() {
        // This test is really just to make sure it doesn't end up in an infinite loop
        val listOfSameFlashcard = listOf(flashcard1, flashcard1)
        presenter = createPresenter()
        presenter.start()
        val inOrder = inOrder(randomFlashcardView)
        val repoInOrder = inOrder(flashcardRepository)
        repoInOrder.verify(flashcardRepository).getFlashcards(callbackCaptor.capture())
        inOrder.verify(randomFlashcardView).setLoadingIndicator(true)
        callbackCaptor.firstValue.onFlashcardsLoaded(listOfSameFlashcard)
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)

        presenter.loadNewFlashcard()
        repoInOrder.verify(flashcardRepository).getFlashcards(callbackCaptor.capture())
        inOrder.verify(randomFlashcardView).setLoadingIndicator(true)
        callbackCaptor.secondValue.onFlashcardsLoaded(listOfSameFlashcard)
        inOrder.verify(randomFlashcardView).setLoadingIndicator(false)

        verify(randomFlashcardViewModel, times(2))
                .setFlashcard(flashcardCaptor.capture())
        val firstFlashcardFrontShown = flashcardCaptor.firstValue
        val secondFlashcardFrontShown = flashcardCaptor.secondValue
        assertEquals(firstFlashcardFrontShown, secondFlashcardFrontShown)
    }

    @Test
    fun turnFlashcard_showsBackInView() {
        createAndStartPresenterAndSetCallbackCaptor()
        callbackCaptor.firstValue.onFlashcardsLoaded(getSingleFlashcardList())
        whenever(randomFlashcardViewModel.getFlashcardSide()).thenReturn(FlashcardSide.FRONT)
        val inOrder = inOrder(randomFlashcardViewModel)
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.FRONT)
        presenter.turnFlashcard()
        inOrder.verify(randomFlashcardViewModel).setFlashcardSide(FlashcardSide.BACK)
    }

    @Test
    fun onNoDataNotAvailable_showsFailedToLoadFlashcardInView() {
        createAndStartPresenterAndSetCallbackCaptor()
        callbackCaptor.firstValue.onDataNotAvailable()
        verify(randomFlashcardView).showFailedToLoadFlashcard()
    }

    @Test
    fun onEmptyFlashcardList_showsFailedToLoadFlashcardInView() {
        createAndStartPresenterAndSetCallbackCaptor()
        callbackCaptor.firstValue.onFlashcardsLoaded(emptyList())
        verify(randomFlashcardView).showFailedToLoadFlashcard()
    }

    private fun getSingleFlashcardList(): List<Flashcard> {
        return listOf(flashcard1)
    }

    private fun getFlashcardList(): List<Flashcard> {
        return listOf(flashcard1, flashcard2)
    }

    private fun createAndStartPresenterAndSetCallbackCaptor() {
        presenter = createPresenter()
        presenter.start()
        verify(flashcardRepository).getFlashcards(callbackCaptor.capture())
    }

    private fun createPresenter(): RandomFlashcardPresenter {
        return RandomFlashcardPresenter(UseCaseHandler(TestUseCaseScheduler()),
                randomFlashcardView, randomFlashcardViewModel,
                GetRandomFlashcard(flashcardRepository))
    }
}
