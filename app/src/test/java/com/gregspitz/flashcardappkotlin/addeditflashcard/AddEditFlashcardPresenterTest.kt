package com.gregspitz.flashcardappkotlin.addeditflashcard

import com.gregspitz.flashcardappkotlin.TestUseCaseScheduler
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.addeditflashcard.domain.usecase.SaveFlashcard
import com.gregspitz.flashcardappkotlin.data.model.Flashcard
import com.gregspitz.flashcardappkotlin.data.source.FlashcardDataSource
import com.gregspitz.flashcardappkotlin.data.source.FlashcardRepository
import com.gregspitz.flashcardappkotlin.flashcarddetail.domain.usecase.GetFlashcard
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Captor

/**
 * Tests for the implementation of {@link AddEditFlashcardPresenter}
 */
class AddEditFlashcardPresenterTest {

    private val flashcard = Flashcard("0", "Front", "Back")

    private val flashcardRepository: FlashcardRepository = mock()

    private val view: AddEditFlashcardContract.View = mock()

    @Captor
    private val getCallbackCaptor = argumentCaptor<FlashcardDataSource.GetFlashcardCallback>()

    @Captor
    private val saveCallbackCaptor = argumentCaptor<FlashcardDataSource.SaveFlashcardCallback>()

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
        createAndStartPresenterAndSetCallbackCaptor()
        val inOrder = inOrder(view)
        inOrder.verify(view).setLoadingIndicator(true)
        getCallbackCaptor.firstValue.onFlashcardLoaded(flashcard)
        inOrder.verify(view).setLoadingIndicator(false)
        verify(view).showFlashcard(flashcard)
    }

    @Test
    fun flashcardNotAvailable_showsFailedToLoadInView() {
        createAndStartPresenterAndSetCallbackCaptor()
        val inOrder = inOrder(view)
        inOrder.verify(view).setLoadingIndicator(true)
        getCallbackCaptor.firstValue.onDataNotAvailable()
        inOrder.verify(view).setLoadingIndicator(false)
        verify(view).showFailedToLoadFlashcard()
    }

    @Test
    fun saveFlashcard_savesToRepositoryAndShowsSaveSuccessInView() {
        createAndStartPresenterAndSetCallbackCaptor()
        getCallbackCaptor.firstValue.onFlashcardLoaded(flashcard)
        presenter.saveFlashcard(flashcard)
        verify(flashcardRepository).saveFlashcard(eq(flashcard), saveCallbackCaptor.capture())
        saveCallbackCaptor.firstValue.onSaveSuccessful()
        verify(view).showSaveSuccessful()
    }

    @Test
    fun saveFailed_showSaveFailedInView() {
        createAndStartPresenterAndSetCallbackCaptor()
        getCallbackCaptor.firstValue.onFlashcardLoaded(flashcard)
        presenter.saveFlashcard(flashcard)
        verify(flashcardRepository).saveFlashcard(eq(flashcard), saveCallbackCaptor.capture())
        saveCallbackCaptor.firstValue.onSaveFailed()
        verify(view).showSaveFailed()
    }

    @Test
    fun showList_callsShowListViewOnView() {
        presenter = createPresenter()
        presenter.showList()
        verify(view).showFlashcardList()
    }

    private fun createAndStartPresenterAndSetCallbackCaptor() {
        presenter = createPresenter()
        presenter.start()
        verify(flashcardRepository).getFlashcard(eq(flashcard.id), getCallbackCaptor.capture())
    }

    private fun createPresenter(): AddEditFlashcardPresenter {
        return AddEditFlashcardPresenter(UseCaseHandler(TestUseCaseScheduler()),
                view, GetFlashcard(flashcardRepository), SaveFlashcard(flashcardRepository))
    }
}
