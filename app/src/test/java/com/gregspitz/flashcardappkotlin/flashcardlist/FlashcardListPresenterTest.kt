package com.gregspitz.flashcardappkotlin.flashcardlist

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
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

    private val mFlashcardRepository : FlashcardRepository = mock()

    private val mFlashcardListView : FlashcardListContract.View = mock()

    private val mCallbackArgumentCaptor = argumentCaptor<FlashcardDataSource.GetFlashcardsCallback>()

    lateinit var mFlashcardListPresenter: FlashcardListPresenter

    @Before
    fun setup() {
        whenever(mFlashcardListView.isActive()).thenReturn(true)
    }

    @Test
    fun creation_setsPresenterOnView() {
        mFlashcardListPresenter = createPresenter()
        verify(mFlashcardListView).setPresenter(mFlashcardListPresenter)
    }

    @Test
    fun startup_showsFlashcardListInView() {
        createAndStartPresenterAndSetGetFlashcardsCallbackCaptor()
        val inOrder = inOrder(mFlashcardListView)
        inOrder.verify(mFlashcardListView).setLoadingIndicator(true)
        // Trigger callback with list of flashcards
        val flashcards = getTestFlashcardsList()
        mCallbackArgumentCaptor.firstValue.onFlashcardsLoaded(flashcards)
        inOrder.verify(mFlashcardListView).setLoadingIndicator(false)
        verify(mFlashcardListView).showFlashcards(flashcards)
    }

    @Test
    fun noFlashcardsToLoad_showsFailedToLoadInView() {
        createAndStartPresenterAndSetGetFlashcardsCallbackCaptor()
        mCallbackArgumentCaptor.firstValue.onDataNotAvailable()
        verify(mFlashcardListView).showFailedToLoadFlashcards()
    }

    @Test
    fun emptyListOfFlashcards_showsNoFlashcardsToLoadInView() {
        createAndStartPresenterAndSetGetFlashcardsCallbackCaptor()
        mCallbackArgumentCaptor.firstValue.onFlashcardsLoaded(emptyList())
        verify(mFlashcardListView).showNoFlashcardsToLoad()
    }

    @Test
    fun addFlashcard_showsAddFlashcard() {
        createAndStartPresenterAndSetGetFlashcardsCallbackCaptor()
        mFlashcardListPresenter.addFlashcard()
        verify(mFlashcardListView).showAddFlashcard()
    }

    @Test
    fun selectFlashcard_showsFlashcardDetails() {
        createAndStartPresenterAndSetGetFlashcardsCallbackCaptor()
        mFlashcardListPresenter.selectFlashcard(flashcard1)
        verify(mFlashcardListView).showFlashcardDetailsUi(flashcard1.id)
    }

    private fun getTestFlashcardsList(): List<Flashcard> {
        return Arrays.asList(flashcard1, flashcard2)
    }

    private fun createAndStartPresenterAndSetGetFlashcardsCallbackCaptor() {
        mFlashcardListPresenter = createPresenter()
        mFlashcardListPresenter.start()
        verify(mFlashcardRepository).getFlashcards(mCallbackArgumentCaptor.capture())
    }

    private fun createPresenter(): FlashcardListPresenter {
        return FlashcardListPresenter(
                UseCaseHandler(TestUseCaseScheduler()),
                mFlashcardListView, GetFlashcards(mFlashcardRepository))
    }
}
