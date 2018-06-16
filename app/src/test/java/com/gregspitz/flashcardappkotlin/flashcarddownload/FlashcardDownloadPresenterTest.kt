package com.gregspitz.flashcardappkotlin.flashcarddownload

import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_1
import com.gregspitz.flashcardappkotlin.TestData.CATEGORY_2
import com.gregspitz.flashcardappkotlin.TestData.FLASHCARD_LIST
import com.gregspitz.flashcardappkotlin.UseCase
import com.gregspitz.flashcardappkotlin.UseCaseHandler
import com.gregspitz.flashcardappkotlin.data.model.Category
import com.gregspitz.flashcardappkotlin.data.service.model.DownloadCategory
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.DownloadFlashcards
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.GetDownloadCategories
import com.gregspitz.flashcardappkotlin.flashcarddownload.domain.usecase.SaveFlashcards
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FlashcardDownloadPresenterTest {

    private val view: FlashcardDownloadContract.View = mock()

    private val inOrder = inOrder(view)

    // GetDownloadCategories use case
    private val getDownloadCategories: GetDownloadCategories = mock()
    private val getDownloadCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<GetDownloadCategories.ResponseValue>>()
    private val downloadCategories =
            listOf(DownloadCategory(CATEGORY_1.name, 2), DownloadCategory(CATEGORY_2.name, 4))
    private val downloadCategoryFlexItems =
            downloadCategories.map { DownloadCategoryFlexItem(it) }
    private val getDownloadResponse =
            GetDownloadCategories.ResponseValue(downloadCategoryFlexItems)

    // DownloadFlashcards use case
    private val downloadFlashcards: DownloadFlashcards = mock()
    private val downloadRequestCaptor =
            argumentCaptor<DownloadFlashcards.RequestValues>()
    private val downloadCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<DownloadFlashcards.ResponseValue>>()
    private val downloadResponse =
            DownloadFlashcards.ResponseValue(FLASHCARD_LIST)

    // SaveFlashcards use case
    private val saveFlashcards: SaveFlashcards = mock()
    private val saveRequestCaptor =
            argumentCaptor<SaveFlashcards.RequestValues>()
    private val saveCallbackCaptor =
            argumentCaptor<UseCase.UseCaseCallback<SaveFlashcards.ResponseValue>>()

    private val useCaseHandler: UseCaseHandler = mock()

    private lateinit var presenter: FlashcardDownloadPresenter

    @Before
    fun setup() {
        whenever(view.isActive()).thenReturn(true)
        presenter = FlashcardDownloadPresenter(useCaseHandler, view, getDownloadCategories,
                downloadFlashcards, saveFlashcards)
    }

    @Test
    fun onCreation_setsSelfOnView() {
        verify(view).setPresenter(presenter)
    }

    @Test
    fun onLoadDownloadCategories_successFromUseCase_callsShowCategoriesOnView() {
        presenter.loadDownloadCategories()
        verifyLoadDownloadCategoriesSuccess()
    }

    @Test
    fun onLoadDownloadCategories_failureFromUseCase_callsFailureOnView() {
        presenter.loadDownloadCategories()
        verifyViewLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(getDownloadCategories), any(),
                getDownloadCallbackCaptor.capture())
        getDownloadCallbackCaptor.firstValue.onError()
        verifyViewLoadingIndicator(false)
        verify(view).showFailedToGetDownloadCategories()
    }

    @Test
    fun onDownloadFlashcards_successFromDownload_successFromSave_callsSuccessOnView() {
        presenter.downloadFlashcards(downloadCategoryFlexItems[0])
        verifyDownloadFlashcardsSuccess()

        verify(useCaseHandler).execute(eq(saveFlashcards), saveRequestCaptor.capture(),
                saveCallbackCaptor.capture())
        assertEquals(FLASHCARD_LIST, saveRequestCaptor.firstValue.flashcards)
        val saveResponse = SaveFlashcards.ResponseValue()
        saveCallbackCaptor.firstValue.onSuccess(saveResponse)

        verifyViewLoadingIndicator(false)
        verify(view).showFlashcardDownloadSuccessful()
    }

    @Test
    fun onDownloadFlashcards_successFromDownload_failureFromSave_callsFailureOnView() {
        presenter.downloadFlashcards(downloadCategoryFlexItems[0])
        verifyDownloadFlashcardsSuccess()

        verify(useCaseHandler).execute(eq(saveFlashcards), saveRequestCaptor.capture(),
                saveCallbackCaptor.capture())
        assertEquals(FLASHCARD_LIST, saveRequestCaptor.firstValue.flashcards)
        saveCallbackCaptor.firstValue.onError()

        verifyViewLoadingIndicator(false)
        verify(view).showFlashcardDownloadFailure()
    }

    @Test
    fun onDownloadFlashcards_failureFromDownload_callsFailureOnView() {
        presenter.downloadFlashcards(downloadCategoryFlexItems[0])
        verifyViewLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(downloadFlashcards), downloadRequestCaptor.capture(),
                downloadCallbackCaptor.capture())
        assertEquals(downloadCategoryFlexItems[0], downloadRequestCaptor.firstValue.category)
        downloadCallbackCaptor.firstValue.onError()
        verifyViewLoadingIndicator(false)
        verify(view).showFlashcardDownloadFailure()
    }

    @Test
    fun onStart_loadsDownloadCategories() {
        presenter.start()
        verifyLoadDownloadCategoriesSuccess()
    }

    private fun verifyViewLoadingIndicator(active: Boolean) {
        inOrder.verify(view).setLoadingIndicator(active)
    }

    private fun verifyLoadDownloadCategoriesSuccess() {
        verifyViewLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(getDownloadCategories), any(),
                getDownloadCallbackCaptor.capture())
        getDownloadCallbackCaptor.firstValue.onSuccess(getDownloadResponse)
        verifyViewLoadingIndicator(false)
        verify(view).showDownloadCategories(eq(downloadCategoryFlexItems))
    }

    private fun verifyDownloadFlashcardsSuccess() {
        verifyViewLoadingIndicator(true)
        verify(useCaseHandler).execute(eq(downloadFlashcards), downloadRequestCaptor.capture(),
                downloadCallbackCaptor.capture())
        assertEquals(downloadCategoryFlexItems[0], downloadRequestCaptor.firstValue.category)
        downloadCallbackCaptor.firstValue.onSuccess(downloadResponse)
    }
}
